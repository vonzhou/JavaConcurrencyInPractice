package com.vonzhou.learn.jcip.pcpp.week5;// A Java 8 streams (non-concurrent) version of the concurrent
// pipeline in TestPipeline.java 

// Not really part of PCPP
// sestoft@itu.dk * 2014-09-23

// A pipeline of transformers connected by streams.  

// This is illustrated by generating URLs, fetching the corresponding
// webpages, scanning the pages for links to other pages, and printing
// those links; using four threads connected by three streams:

// UrlProducer --(Stream<String>)--> 
// PageGetter  --(Stream<Webpage>)--> 
// LinkScanner --(Stream<Link>)--> 
// LinkPrinter

// For streams
import java.util.stream.Stream;

// For reading webpages
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// For regular expressions
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class TestStreams {
  public static void main(String[] args) throws IOException {
    Stream<String> urlStream 
      = Stream.of(urls).parallel();
    Stream<Webpage> pageStream 
      = urlStream.flatMap(url -> makeWebPageOrNone(url, 200));
    Stream<Link> linkStream 
      = pageStream.flatMap(page -> makeLinks(page));
    linkStream.forEach(link -> System.out.printf("%s links to %s%n", link.from, link.to));
  }

  private static final String[] urls = 
  { "http://www.itu.dk", "http://www.di.ku.dk", "http://www.miele.de",
    "http://www.microsoft.com", "http://www.amazon.com", "http://www.dr.dk",
    "http://www.vg.no", "http://www.tv2.dk", "http://www.google.com",
    "http://www.ing.dk", "http://www.dtu.dk", "http://www.bbc.co.uk"
  };

  private static Stream<Webpage> makeWebPageOrNone(String url, int maxLines) {
    try { return Stream.of(new Webpage(url, getPage(url, maxLines))); }
    catch (IOException exn) {
      System.out.println(exn); 
      return Stream.empty();
    }
  }

  private static String getPage(String url, int maxLines) throws IOException {
    // This will close the streams after use (JLS 8 para 14.20.3):
    try (BufferedReader in 
         = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
      StringBuilder sb = new StringBuilder();
      for (int i=0; i<maxLines; i++) {
        String inputLine = in.readLine();
        if (inputLine == null)
          break;
        else
        sb.append(inputLine).append("\n");
      }
      return sb.toString();
    }
  }

  private final static Pattern urlPattern 
    = Pattern.compile("a href=\"(\\p{Graph}*)\"");

  private static Stream<Link> makeLinks(Webpage page) {
    final Stream.Builder<Link> builder = Stream.builder();
    final Matcher urlMatcher = urlPattern.matcher(page.contents);
    while (urlMatcher.find()) {
      String link = urlMatcher.group(1);
      builder.accept(new Link(page.url, link));
    }
    return builder.build();
  }
}

class Webpage {
  public final String url, contents;
  public Webpage(String url, String contents) {
    this.url = url;
    this.contents = contents;
  }
}

class Link {
  public final String from, to;
  public Link(String from, String to) {
    this.from = from;
    this.to = to;
  }

  public int hashCode() {
    return (from == null ? 0 : from.hashCode()) * 37
         + (to == null ? 0 : to.hashCode());
  }

  public boolean equals(Object obj) {
    Link that = obj instanceof Link ? (Link)obj : null;
    return that != null 
      && (from == null ? that.from == null : from.equals(that.from))
      && (to == null ? that.to == null : to.equals(that.to));
  }
}
