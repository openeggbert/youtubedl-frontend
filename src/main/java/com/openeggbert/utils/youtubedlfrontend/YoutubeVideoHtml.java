///////////////////////////////////////////////////////////////////////////////////////////////
// youtubedl-frontend: Tool generating html pages for Archive Box.
// Copyright (C) 2024 the original author or authors.
//
// This program is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation, either version 3
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see 
// <https://www.gnu.org/licenses/> or write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
///////////////////////////////////////////////////////////////////////////////////////////////
package com.openeggbert.utils.youtubedlfrontend;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.Getter;

/**
 *
 * @author robertvokac
 */
public class YoutubeVideoHtml {

    @Getter
    private final String singleVideo;

    public YoutubeVideoHtml(YoutubeVideo youtubeVideo, File archiveBoxRootDirectory, File archiveBoxArchiveDirectory, long countOfVideosInChannel) {

        StringBuilder videoHtml = new StringBuilder("""
                                                             <!DOCTYPE html>
                                                            <html>
                                                            <head>
                                                            <meta charset="UTF-8">
                                                            <link rel="icon" type="image/x-icon" href="../favicon.ico" sizes="16x16">
                                                            <title>"""
                + youtubeVideo.getTitle()
                + """
                                                                    </title>
                                                                    <style>
                                                                    body {padding:20px;}
                                                                    * {
                                                                      font-family:Arial;
                                                                    }
                                                  
                                                                    </style>
                                                                    </head>
                                                                    <body>
                                                                    """
        );
        String finalUrl = "https://www.youtube.com/watch?v=" + youtubeVideo.getId();
        videoHtml.append("<input type=\"text\" id=\"youtube_url\" name=\"youtube_url\" size=\"60\" width=\"60\" style=\"margint-bottom:20px;margin-right:10px;font-size:110%;padding:5px;\" value=\"").append(finalUrl).append("\"><br>\n<br>\n");
        videoHtml.append("<a target=\"_blank\" href=\"").append(finalUrl).append("\">");
        videoHtml.append(finalUrl).append("</a>").append("<br>\n");
        String videoLocalFileEncoded = null;
        String videoLocalUrl = "";
        try {
            videoLocalFileEncoded = URLEncoder.encode(youtubeVideo.getVideoFileName(), StandardCharsets.UTF_8.displayName()).replace("+", "%20").replace("#", "%23");
            videoLocalUrl = "file:///" + archiveBoxRootDirectory.getAbsolutePath() + "/archive/" + youtubeVideo.getSnapshot() + "/media/" + videoLocalFileEncoded;
        } catch (UnsupportedEncodingException ex) {
            throw new YoutubedlFrontendException(ex.getMessage());
        }
        if (!youtubeVideo.getVideoFileName().endsWith(".mkv")) {
            videoHtml.append("<video src=\"");

            String videoFileName = youtubeVideo.getVideoFileName();

            {
                try {
                    videoFileName = URLEncoder.encode(youtubeVideo.getVideoFileName(), StandardCharsets.UTF_8.displayName()).replace("+", "%20").replace("#", "%23");
                } catch (UnsupportedEncodingException ex) {
                    System.out.println("File name does not contain : " + videoFileName);
                    System.err.println(ex.getMessage());
                    throw new YoutubedlFrontendException(ex.getMessage());
                }
            }
            videoHtml.append("../archive/").append(youtubeVideo.getSnapshot()).append("/media/").append(//                                    URLEncoder.encode(
                    videoFileName);
            videoHtml.append("""
                                                                                                                        " controls autoplay height=\"440px\">
                                                                                                                        Your browser does not support the video tag.
                                                                                                                        </video><br>
                                                                                                                        """);

        } else {
            videoHtml.append("<a target=\"_blank\" href=\"").append(videoLocalUrl).append("\">");

            videoHtml.append("<img style=\"margin:10px;height:500px;\" src=\"../archive/")
                    .append(youtubeVideo.getSnapshot())
                    .append("/media/thumbnail.")
                    .append(youtubeVideo.getThumbnailFormat())
                    .append("\"></a><br>\n");
        }
        videoHtml.append("<span style=\"font-size:200%;font-weight:bold;\">").append(youtubeVideo.getTitle()).append("</span>");
        videoHtml.append("<br>\n<br>\n");
        videoHtml.append("#").append(youtubeVideo.getNumber()).append("&nbsp;&nbsp;&nbsp;");
        boolean backEnabled = youtubeVideo.getNumber() > 1 && youtubeVideo.getPreviousVideoId() != null;

        {

            videoHtml.append("<button ").append(backEnabled ? "" : "disabled").append(" style=\"").append(backEnabled ? "" : "visibility:hidden;").append("font-size:200%;\" onclick=\"window.location ='").append("./").append(youtubeVideo.getPreviousVideoId()).append(".html'\">");

            videoHtml.append("Back");
            videoHtml.append("</button>");
        }
        videoHtml.append("&nbsp;&nbsp;&nbsp;");
        boolean nextEnabled = youtubeVideo.getNumber() < countOfVideosInChannel && youtubeVideo.getNextVideoId() != null;

        {
            videoHtml.append("<button ").append(nextEnabled ? "" : "disabled").append(" style=\"").append(nextEnabled ? "" : "visibility:hidden;").append("font-size:200%;\" onclick=\"window.location ='").append("./").append(youtubeVideo.getNextVideoId()).append(".html'\">");

            videoHtml.append("Next");

            videoHtml.append("</button>");

        }
        videoHtml.append(" ");
        videoHtml
                .append("<br><br><a href=\"../archive/")
                .append(youtubeVideo.getSnapshot())
                .append("/media/")
                .append(videoLocalFileEncoded)
                .append("\">Download</a> ");
        videoHtml.append(Utils.TWO_DECIMAL_POINTS_FORMATTER.format(((double) youtubeVideo.getVideoFileSizeInBytes()) / 1024d / 1024d)).append(" MB ");
        if (youtubeVideo.getVideoFileName().endsWith(".mkv")) {

            String v = youtubeVideo.getVideoFileName().replaceAll(" ", "\\\\ ");
            v = v.replace("(", "\\(");
            v = v.replace(")", "\\)");
            var vWebm = v.substring(0, v.length() - 3) + "webm";

            videoHtml.append("<input type=\"text\" id=\"archiveBoxArchiveDirectory\" name=\"archiveBoxArchiveDirectory\" size=\"100\" width=\"100\" style=\"margin-bottom:20px;margin-right:10px;font-size:110%;padding:5px;\" value=\"");
            videoHtml.append("cd ").append(archiveBoxArchiveDirectory).append("/").append(youtubeVideo.getSnapshot()).append("/media/");
            videoHtml.append(" && ffmpeg -i ").append(v).append(" -preset slow -crf 18 ").append(vWebm);
            videoHtml.append("\"><br>");

        } else {

            videoHtml.append("<input type=\"text\" id=\"archiveBoxArchiveDirectory\" name=\"archiveBoxArchiveDirectory\" size=\"100\" width=\"100\" style=\"margin-bottom:20px;margin-right:10px;font-size:110%;padding:5px;\" value=\"");
            videoHtml.append(archiveBoxArchiveDirectory).append("/").append(youtubeVideo.getSnapshot()).append("/media/");

            videoHtml.append("\"><br>");
        }

        videoHtml.append("<br>\n<br>\n");
        videoHtml.append("<br>\n");
        videoHtml.append("<pre style=\"white-space: pre-wrap; border:1px solid black;max-width:600px;padding:10px;min-height:50px;\">");
        videoHtml.append(youtubeVideo.getDescription().isBlank() ? "No description" : youtubeVideo.getDescription());
        videoHtml.append("</pre>");
        videoHtml.append("<h2>Comments</h2>");
        youtubeVideo.getComments().forEach(co -> {

            videoHtml.append("<div style=\"margin-left:")
                    .append(co.dotCount() * 50)
                    .append("px;\">");
            videoHtml.append("<h3>").append(co.getAuthor()).append("</h3>");

            videoHtml.append("<span style=\"color:grey;font-size:80%;\">")
                    .append(Utils.DATE_FORMAT.format(new Date(co.getTimestamp() * 1000))).append("</span><br>\n");
            videoHtml.append("<span style=\"color:grey;font-size:80%;\">").append(co.getId()).append(" ")
                    .append(co.getParentId()).append("</span><br>\n");
            videoHtml.append("<pre style=\"white-space: pre-wrap;border:1px solid black;max-width:600px;padding:10px;min-height:50px;\">").append(co.getText()).append("</pre>");
            videoHtml.append("</div>");
        });
        videoHtml.append("</body></html>");
        singleVideo = videoHtml.toString();

    }

    @Override
    public String toString() {
        return this.singleVideo;
    }

}
