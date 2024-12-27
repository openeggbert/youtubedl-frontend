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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

/**
 *
 * @author robertvokac
 */
@Data
@NoArgsConstructor
public class YoutubeComment implements Comparable<YoutubeComment> {

    private String id, parentId, text, author;
    private long timestamp;

    YoutubeComment(JSONObject jsonObject) {
        id = jsonObject.getString("id");
        parentId = jsonObject.getString("parent");
        text = jsonObject.getString("text");
        author = jsonObject.getString("author");
        timestamp = jsonObject.getInt("timestamp");
    }

    public static List<YoutubeComment> sort(List<YoutubeComment> list) {

        List<YoutubeComment> root = getChildren(list, "root");
        Collections.sort(root);
        return list;
    }

    private static List<YoutubeComment> getChildren(List<YoutubeComment> all, String parentId) {
        final List<YoutubeComment> children = all.stream().filter(c -> c.getParentId().equals(parentId)).sorted().toList();
        List<YoutubeComment> result = new ArrayList<>();
        children.stream().forEach(c -> {
            result.add(c);
            result.addAll(getChildren(all, c.getId()));
        });
        return result;
    }

    @Override
    public int compareTo(YoutubeComment o) {
        return Long.valueOf(this.timestamp).compareTo(o.timestamp);
    }

    public int dotCount() {
        int i = 0;
        for (char ch : getId().toCharArray()) {
            if (ch == '.') {
                i++;
            }
        }
        return i;
    }

}
