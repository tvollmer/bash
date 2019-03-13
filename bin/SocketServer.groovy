def server = new ServerSocket(5000)
while(true){

    server.accept() { socket ->

        socket.withStreams{ input, output ->

            w = new PrintWriter(output)
            //w << "What is your name?"
            //w.flush()

            r = input.newReader()
            try{
                while ((fromClient = r.readLine()) != null){
                    println "Server: ${fromClient}, thread: ${Thread.currentThread()}"
                    w << "hello ${fromClient}\r\nWhat is your name?"
                    w.flush()
                }
            }
            catch(IOException ex){
                println "had some error."
            }
            w.close()
        }
    }
}
