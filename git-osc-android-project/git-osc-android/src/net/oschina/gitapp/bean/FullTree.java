/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.gitapp.bean;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import android.text.TextUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 代码树结构
 * @created 2014-06-04
 * @author 火蚁（http://my.oschina/LittleDY）
 *
 */
public class FullTree {

    /**
     * 根文件夹
     */
    public final Folder root;

    /**
     * Create tree with branch
     *
     * @param tree
     * @param reference
     */
    public FullTree(List<CodeTree> entries) {
        root = new Folder();
        if (entries != null && !entries.isEmpty())
            for (CodeTree entry : entries)
                root.add(entry);
    }
	
    /**
     * 文件夹
     */
    public static class Folder extends Entry {

        /**
         * Sub folders
         */
        public final Map<String, Folder> folders = new TreeMap<String, Folder>(
                CASE_INSENSITIVE_ORDER);

        /**
         * Files
         */
        public final Map<String, Entry> files = new TreeMap<String, Entry>(
                CASE_INSENSITIVE_ORDER);

        private Folder() {
            super();
        }

        private Folder(CodeTree entry, Folder parent) {
            super(entry, parent);
        }

        private void addFile(CodeTree entry, String[] pathSegments, int index) {
            if (index == pathSegments.length - 1) {
                Entry file = new Entry(entry, this);
                files.put(file.name, file);
            } else {
                Folder folder = folders.get(pathSegments[index]);
                if (folder != null)
                    folder.addFile(entry, pathSegments, index + 1);
            }
        }

        private void addFolder(CodeTree entry, String[] pathSegments, int index) {
            if (index == pathSegments.length - 1) {
                Folder folder = new Folder(entry, this);
                folders.put(folder.name, folder);
            } else {
                Folder folder = folders.get(pathSegments[index]);
                if (folder != null)
                    folder.addFolder(entry, pathSegments, index + 1);
            }
        }

        private void add(final CodeTree entry) {
            String type = entry.getType();
            String path = entry.getPath();
            if (TextUtils.isEmpty(path))
                return;

            if (CodeTree.TYPE_BLOB.equals(type)) {
                String[] segments = path.split("/");
                if (segments.length > 1) {
                    Folder folder = folders.get(segments[0]);
                    if (folder != null)
                        folder.addFile(entry, segments, 1);
                } else if (segments.length == 1) {
                    Entry file = new Entry(entry, this);
                    files.put(file.name, file);
                }
            } else if (CodeTree.TYPE_TREE.equals(type)) {
                String[] segments = path.split("/");
                if (segments.length > 1) {
                    Folder folder = folders.get(segments[0]);
                    if (folder != null)
                        folder.addFolder(entry, segments, 1);
                } else if (segments.length == 1) {
                    Folder folder = new Folder(entry, this);
                    folders.put(folder.name, folder);
                }
            }
        }
    }
    
    /**
     * Entry in a tree
     */
    public static class Entry implements Comparable<Entry> {

        /**
         * Parent folder
         */
        public final Folder parent;

        /**
         * Raw tree entry
         */
        public final CodeTree entry;

        /**
         * Name
         */
        public final String name;

        private Entry() {
            this.parent = null;
            this.entry = null;
            this.name = null;
        }

        private Entry(CodeTree entry, Folder parent) {
            this.entry = entry;
            this.parent = parent;
            this.name = entry.getPath();
        }

        @Override
        public int compareTo(Entry another) {
            return CASE_INSENSITIVE_ORDER.compare(name, another.name);
        }
    }
}
