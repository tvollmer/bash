#!/usr/bin/env groovy

@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.2' )

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

//http://www.webservicex.net/CurrencyConvertor.asmx?WSDL
new HTTPBuilder('http://www.webservicex.net/stockquote.asmx').request(POST, XML) { req ->
  headers."Content-Type" = "application/soap+xml; charset=utf-8"
  headers."Accept" = "application/soap+xml; charset=utf-8"
  body = """<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:web="http://www.webserviceX.NET/">
   <soap:Header/>
   <soap:Body>
      <web:GetQuote>
         <web:symbol>^RUT, AAPL, IBM, ^RVX</web:symbol>
      </web:GetQuote>
   </soap:Body>
</soap:Envelope>"""

  response.success = { resp, xmlRaw ->
    println 'request was successful'
    println "xmlRaw : $xmlRaw"
    def xml = new XmlSlurper().parseText(xmlRaw.toString())
    println "body : ${xml}"
    xml.Stock.each{ stock ->
        println "Symbol:${stock.Symbol}, last:${stock.Last}, name:${stock.Name}"
    }
    assert resp.status < 400
  }
   
  response.failure = { resp ->
    println 'request failed'
    assert resp.status >= 400
  }
}
