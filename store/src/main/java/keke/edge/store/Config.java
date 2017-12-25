package keke.edge.store;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import keke.edge.util.JsonData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author keke
 */
public class Config extends JsonData {

    public Config() {
        this(new JsonObject());
    }

    public Config(JsonObject jsonObject) {
        super(jsonObject);
    }

    public long getTimestamp() {
        return getJsonObject().getLong("timestamp");
    }

    public void setTimestamp(long timestamp) {
        getJsonObject().put("timestamp", timestamp);
    }

    public List<Folder> getFolders() {
        JsonArray folderData = getJsonObject().getJsonArray("folders");
        List<Folder> folders = new ArrayList<>();
        folderData.forEach(s -> folders.add(new Folder((JsonObject) s)));
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        JsonArray folderData = new JsonArray();
        folders.forEach(f -> folderData.add(f.data));
        getJsonObject().put("folders", folderData);
    }

    public static class Folder {
        private JsonObject data;

        public Folder(JsonObject data) {
            this.data = data;
        }

        public String getPath() {
            return data.getString("path");
        }

        public void setPath(String path) {
            data.put("path", path);
        }

        public String[] getExts() {
            ArrayList<String> list = new ArrayList<>();
            data.getJsonArray("exts").forEach(s -> list.add(s.toString()));
            return list.toArray(new String[0]);
        }

        public void setExts(String[] exts) {
            JsonArray ary = new JsonArray(Arrays.asList(exts));
            data.put("exts", ary);

        }
    }
}
