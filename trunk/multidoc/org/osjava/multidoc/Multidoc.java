package org.osjava.multidoc;

import java.io.*;
import java.util.*;

import com.generationjava.io.xml.*;

public class Multidoc {

    // multidoc multidoc.conf target/multidoc
    public static void main(String[] args) throws IOException {

        FileReader reader = new FileReader(args[0]);
        XMLParser parser = new XMLParser();
        XMLNode node = parser.parseXML(new BufferedReader(reader));
        // node is <site>
        String title = node.getAttr("name");
        String style = node.getAttr("style");
        String url = node.getAttr("url");
        System.out.println("Multidocing for "+title);
        DocumentSite site = new DocumentSite(url, title, style);
        Enumeration docEnum = node.enumerateNode("document");
        // hacked to do just the first one
        while(docEnum.hasMoreElements()) {
            XMLNode subnode = (XMLNode) docEnum.nextElement();
            String type = subnode.getAttr("type");
            String name = subnode.getAttr("name");
            Document document = new Document(type, name);
            site.addDocument(name, document);
            DocumentProjectCreator creator = Multidoc.getCreator(type);
            Enumeration uriEnum = subnode.enumerateNode("uri");
            while(uriEnum.hasMoreElements()) {
                XMLNode urinode = (XMLNode) uriEnum.nextElement();
                String uri = urinode.getValue();
                System.out.println("Loading: "+uri);
                DocumentProject project = creator.create(uri);
                if(project == null) {
                    System.err.println("WARN: null project found: "+uri);
                    continue;
                }
                document.addProject(project);
            }
        }

        // target directory
        File target = new File(args[1]);
        target.mkdirs();

        Collection names = site.getNames();
        Iterator iterator = names.iterator();
        while(iterator.hasNext()) {
            String name = (String) iterator.next();
            Document document = site.getDocument(name);
            MultidocGenerator generator = getGenerator(document.getType());
            File subtarget = new File(target, name);
            subtarget.mkdirs();
            System.out.println("Generating for: "+name);
            generator.generate( subtarget, site, document );
        }

        System.out.println("Generating menu");
        generateMenu( target, site );

    }

    public static DocumentProjectCreator getCreator(String type) {
        if("Javadoc".equals(type)) {
            return new org.osjava.multidoc.creators.JavadocCreator();
        }
        if("XRef".equals(type)) {
            return new org.osjava.multidoc.creators.XRefCreator();
        }
        if("JCoverage".equals(type)) {
            return new org.osjava.multidoc.creators.JCoverageCreator();
        }
        return null;
    }

    public static MultidocGenerator getGenerator(String type) {
        if("Javadoc".equals(type)) {
            return new org.osjava.multidoc.generators.JavadocMultidocGenerator();
        }
        if("XRef".equals(type)) {
            return new org.osjava.multidoc.generators.JavadocMultidocGenerator();
        }
        if("JCoverage".equals(type)) {
            return new org.osjava.multidoc.generators.JavadocMultidocGenerator();
        }
        return null;
    }

    public static void generateMenu(File targetDirectory, DocumentSite site) throws IOException {
        FileWriter fw = new FileWriter( new File( targetDirectory, "menu.html") );
        fw.write("<html><head><LINK REL ='stylesheet' TYPE='text/css' HREF='");
        fw.write(site.getStylesheet());
        fw.write("' TITLE='Style'>\n");
        fw.write("<script src='multidoc.js'/>\n");
        fw.write("</head><body>\n");
        fw.write("<table width='100%'><tr><td class='NavBarCell1'>\n");
//        fw.write("<a href='API/overview-summary.html' target='classFrame'><FONT CLASS='NavBarFont1'><b>Overview</b></FONT></a> - \n"); 

        Collection names = site.getNames();
        Iterator iterator = names.iterator();
        while(iterator.hasNext()) {
            String name = (String) iterator.next();
            fw.write("<a href=\"javascript:load('projectListFrame','");
            fw.write(name);
            fw.write("/project-frame.html','classFrame','");
            fw.write(name);
            fw.write("/overview-summary.html','packageListFrame','");
            fw.write(name);
            fw.write("/overview-frame.html','packageFrame','blank.html')\"><FONT CLASS=\"NavBarFont1\"><b>");
            fw.write(name);
            fw.write("</b></FONT></a> - \n");
        }
        fw.write("<a href='help-doc.html' target='classFrame'><FONT CLASS='NavBarFont1'><b>Help</b></FONT></a></td>\n");
        fw.write("<td align='right'><a href='");
        fw.write(site.getUrl());
        fw.write("' target='classFrame'><FONT CLASS='NavBarFont1'><b>");
        fw.write(site.getUrl());
        fw.write("</b></FONT></a>\n");
        fw.write("</td></tr></table>\n");
        fw.write("</body></html>\n");
        fw.close();
    }
}
