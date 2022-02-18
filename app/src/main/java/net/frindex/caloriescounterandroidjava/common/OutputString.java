package net.frindex.caloriescounterandroidjava.common;
/**
 *
 * File: OutputString.java
 * Version 1.0.0
 * Date 18.02.2022
 * Copyright (c) 2022 Sindre Andre Ditlefsen
 * Website: https://ditlef.net
 * License: http://opensource.org/licenses/gpl-license.php GNU Public License
 *
 */
public class OutputString {

    public String outputHTML(String value ){

        value = value.replace("\\&#39;", "'");
        value = value.replace("\\\\&#39;", "'");
        value = value.replace("&#39;", "'");
        value = value.replace("&#039;", "'");

        // Paragraph
        value = value.replace("<p style=\"text-align: left;\">", "");
        value = value.replace("<p>", "");
        value = value.replace("</p>", "");

        // Line shift
        // Line shift should be done in the output, because if we save \n to database then
        // it doesn't get printed as a line separator later
        /*
        value = value.replace("\n\n", System.getProperty("line.separator"));
        value = value.replace("\r\n", System.getProperty("line.separator"));
        value = value.replace("\\r", System.getProperty("line.separator"));
        value = value.replace("\\n", System.getProperty("line.separator"));
        value = value.replace("\r", System.getProperty("line.separator"));
        value = value.replace("\n", System.getProperty("line.separator"));
        value = value.replaceAll("\\\\n", System.getProperty("line.separator"));
        value = value.replaceAll("\\\n", System.getProperty("line.separator"));
        value = value.replaceAll("\\n", System.getProperty("line.separator"));
        value.replaceAll("\\n", System.getProperty("line.separator"));*/

        value = value.replace("&#39;", "'");
        value = value.replace("<span>", "");
        value = value.replace("</span>", "");
        value = value.replace("<ul>", "");
        value = value.replace("</ul>", "");
        value = value.replace("<ol>", "");
        value = value.replace("</ol>", "");
        value = value.replace("<li>", "\u2022 ");
        value = value.replace("</li>", "");
        value = value.replace("<strong>", "");
        value = value.replace("</strong>", "");
        value = value.replace("&nbsp;", " ");
        value = value.replace("<em>", "");
        value = value.replace("</em>", "");
        value = value.replace("&frac12;", "1/2");
        value = value.replace("&amp;", "&");
        value = value.replace("&#183;", "•");


        // Fixes
        value = value.replace("<span lang=\\\"no\\\" xml:lang=\\\"no\\\">", "");
        value = value.replace("<span lang=\"no\" xml:lang=\"no\">", "");

        // Norwegian
        value = value.replace("&aelig;", "æ");
        value = value.replace("&oslash;", "ø");
        value = value.replace("&aring;", "å");
        value = value.replace("&Aelig;", "Æ");
        value = value.replace("&Oslash;", "Ø");
        value = value.replace("&Aring;", "Å");

        return value;

    }
}
