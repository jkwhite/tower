/*
    Tower
    Copyright (C) 2007, John K White, All Rights Reserved
*/
/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package org.excelsi.aether;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.io.BufferedInputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.net.MalformedURLException;
import org.yaml.snakeyaml.Yaml;


public class Data {
    private Loader[] _loaders;


    public Data(URL[] urls) throws IOException {
        _loaders = new Loader[urls.length];
        for(int i=0;i<urls.length;i++) {
            URL url = urls[i];
            Loader loader;
            if(url==null) {
                // TODO: Need to preload all resources that will be requested by dynamic classes
                // due to bug in WebStart.
                // See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6326591
                loader = new ClassLoaderLoader("universe", "pcs", "npcs", "items",
                    "basins", "corpses", "encumbrance", "gold", "hunger", "regen",
                    "shrines", "writing");
            }
            else if(url.getProtocol().equals("http")) {
                loader = new HTTPLoader(url);
            }
            else {
                File f = new File(url.getFile());
                if(f.isDirectory()) {
                    loader = new FileLoader(f);
                }
                else {
                    loader = new JarLoader(new JarFile(f));
                }
            }
            _loaders[i] = loader;
        }
    }

    public InputStream[] getResources(String name) throws IOException {
        List<InputStream> ins = new ArrayList<InputStream>();
        for(Loader l:_loaders) {
            //System.err.println("checking "+l+" for "+name);
            InputStream is = l.getInputStream(name);
            if(is!=null) {
                ins.add(is);
            }
        }
        //System.err.println("returning "+ins);
        return ins.toArray(new InputStream[ins.size()]);
    }

    public InputStream getResource(String name) throws IOException {
        for(Loader l:_loaders) {
            //System.err.println("checking "+l+" for "+name);
            InputStream is = l.getInputStream(name);
            if(is!=null) {
                return is;
            }
        }
        throw new IOException("no such resource '"+name+"'");
    }

    public static final String resource(final String url) {
        try {
            final URI uri = Data.class.getResource(url).toURI();
            final FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<String,String>(){{ put("create", "true");}});
            return new String(Files.readAllBytes(Paths.get(uri)), "UTF-8");
        }
        catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <E> E loadYaml(final String url) {
        try(final InputStream is = Data.class.getResourceAsStream(url)) {
            if(is==null) {
                throw new IllegalArgumentException("no such resource '"+url+"'");
            }
            return (E) new Yaml().load(is);
        }
        catch(IOException e) {
            throw new IllegalStateException("failed reading '"+url+"': "+e, e);
        }
    }

    interface Loader {
        InputStream getInputStream(String name) throws IOException;
    }

    static class ClassLoaderLoader implements Loader {
        private Map<String,InputStream> _preloaded = new HashMap<String,InputStream>();

        public ClassLoaderLoader(String... preload) {
            if(preload!=null) {
                for(String pre:preload) {
                    _preloaded.put(pre, getClass().getClassLoader().getResourceAsStream(pre));
                }
            }
        }

        public InputStream getInputStream(String name) throws IOException {
            if(_preloaded.containsKey(name)) {
                return _preloaded.get(name);
            }
            else {
                InputStream is = getClass().getClassLoader().getResourceAsStream(name);
                //if(is==null) {
                    //throw new IOException("no such resource '"+name+"'");
                //}
                return is;
            }
        }

        public String toString() {
            return "ClassLoaderLoader ["+getClass().getClassLoader()+"]";
        }
    }

    static class JarLoader implements Loader {
        private JarFile _file;


        public JarLoader(JarFile f) {
            _file = f;
        }

        public InputStream getInputStream(String name) throws IOException {
            for(Enumeration<JarEntry> en=_file.entries();en.hasMoreElements();) {
                JarEntry e = en.nextElement();
                if(e.getName().equals(name)) {
                    return new BufferedInputStream(_file.getInputStream(e));
                }
            }
            //throw new IOException("no such resource '"+name+"'");
            return null;
        }

        public String toString() {
            return "JarLoader ["+_file.getName()+"]";
        }
    }

    static class FileLoader implements Loader {
        private File _dir;


        public FileLoader(File f) {
            _dir = f;
        }

        public InputStream getInputStream(String name) throws IOException {
            File f = new File(_dir, name);
            if(f.exists()) {
                return new BufferedInputStream(new FileInputStream(f));
            }
            else {
                //throw new IOException("no such resource '"+name+"'");
                return null;
            }
        }

        public String toString() {
            return "FileLoader ["+_dir+"]";
        }
    }

    static class HTTPLoader implements Loader {
        private URL _base;


        public HTTPLoader(URL base) {
            _base = base;
        }

        public InputStream getInputStream(String name) throws IOException {
            try {
                return new URL(_base+"/"+name).openStream();
            }
            catch(MalformedURLException e) {
                IOException ex = new IOException();
                ex.initCause(e);
                throw ex;
            }
        }

        public String toString() {
            return "HTTPLoader ["+_base+"]";
        }
    }
}
