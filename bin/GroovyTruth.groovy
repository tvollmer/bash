class Account {  
     
     String status  
     boolean asBoolean(){  
          if(!'active'.equals(status))   
               return false  
          else   
               return true  
     }  
}  
println (new Account(status:'active') ? "true" : "false") // prints true
println (new Account(status:'prospect') ? "true" : "false") // prints true
assert new Account(status:'active')  
assert !new Account(status:'prospect')

