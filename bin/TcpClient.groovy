s = new Socket("localhost", 5000)
s << "Hello there.\n"
//Thread.sleep(1000)
/*
s.withStreams{ input, output ->
    w = new PrintWriter(output)
    w << "Tim\n"
    w.flush()

    r = input.newReader()
    try{
        while ((fromServer = r.readLine()) != null){
            println "Client: ${fromServer}"
        }
    }
    catch(IOException ex){
        println "had some error."
    }
}*/
s.close()
