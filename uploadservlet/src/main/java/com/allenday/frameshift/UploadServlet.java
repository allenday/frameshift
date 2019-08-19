package com.allenday.frameshift;

import com.allenday.image.ImageProcessor;
import com.allenday.image.ImageFeatures;

import java.lang.StringBuffer;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;


@SuppressWarnings("serial")
@MultipartConfig(location="/tmp/upload", fileSizeThreshold=1024*1024, maxFileSize=1024*1024*50)
@WebServlet(urlPatterns={"/upload"}, name="upload")
public class UploadServlet extends HttpServlet {
  private ImageProcessor processor = new ImageProcessor(8,3,false);
  private String urlString = "http://localhost:8984/solr/frameshift";
  private SolrClient solr = new HttpSolrClient.Builder(urlString).build();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/plain");
    PrintWriter out = resp.getWriter();

    //processor.clearFiles();
        
    int i = 0;
    String file_id = null;
    String rgbtc = null;
    String time_offset = null;
    for(Part part: req.getParts()) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream()));
      String line;
      StringBuffer sb = new StringBuffer();
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }

      if ( part.getName().equals("file_id") ) {
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
        file_id = sb.toString();
      }

      else if ( part.getName().equals("time_offset") ) {
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
        time_offset = sb.toString();
      }

      else if ( part.getName().equals("file") ) {
        String upfile = String.format("part-%05d.jpg",(int) Math.ceil(Math.random() * 99999));
        part.write(upfile);
        ImageFeatures features = processor.extractFeatures(new File("/tmp/upload/"+upfile));
        //processor.processImages();

        //for (Entry<File,ImageFeatures> e : processor.getResults().entrySet()) {
        //  File image = e.getKey();
        //  ImageFeatures features = e.getValue();
          rgbtc = features.getTokensAll();
          //rgbtc = features.getRcompact()
          //      + features.getGcompact()
          //      + features.getBcompact()
          //      + features.getTcompact()
          //      + features.getCcompact();
          //rgbtc = features.getRtokens()
          //      + features.getGtokens()
          //      + features.getBtokens()
          //      + features.getTtokens()
          //      + features.getCtokens();

          //out.print( "[" + features.getRcompact() + "] " );
          //out.print( "[" + features.getGcompact() + "] " );
          //out.print( "[" + features.getBcompact() + "] " );
          //out.print( "[" + features.getTcompact() + "] " );
          //out.print( "[" + features.getCcompact() + "] " );
          //out.print( "\n" );

          //System.err.println( image + "\t" + features.getRtokens() );
          //System.err.println( image + "\t" + features.getGtokens() );
          //System.err.println( image + "\t" + features.getBtokens() );
          //System.err.println( image + "\t" + features.getTtokens() );
          //System.err.println( image + "\t" + features.getCtokens() );
        //}
      }
    }
    //out.printf("Got part: name=%s, size=%d%n",part.getName(), part.getSize());
    //part.write(String.format("part-%02d.dat",i++));

    try {
      SolrInputDocument document = new SolrInputDocument();
      //document.addField("id", "552199");
      document.addField("rgbtc", rgbtc);
      document.addField("file_id", file_id);
      document.addField("time_offset", time_offset);

      UpdateResponse response = solr.add(document);
      solr.commit();
    } catch (SolrServerException sse) {
      throw new IOException("SolrServerException: " + sse.getStackTrace());
    }
  }
}
