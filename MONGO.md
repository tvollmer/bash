1. Run Mongo shell
    ```
    mongo
    ```
1. Create users
    1. Create admin
        ```
        use admin
        db.createUser(  {  user: "fooDbAdmin", pwd: "Bar",  roles: [ { role: "userAdminAnyDatabase", db: "admin" }, { role: "dbOwner", db: "other-db" } ] } )
        ```
    1. Enable Auth
        * Edit /etc/mongod.conf to uncomment #security settings.
        * Note: authorization prop is under security and requires two spaces.
            ```
            security:
                authorization: enabled
            ```
    1. Restart Mongod
    1. Create Api and Reader for Other Database
        ```
        use admin
        db.auth( { user: "fooDbAdmin", pwd: "Bar"});

        use other-db
        db.createUser({ user: "fooApi", pwd: "aw3s0m3", roles: [{ role: "readWrite", db: "other-db" }]})
        db.createUser({ user: "fooDbReader", pwd: "u-guessed-it", roles: [{ role: "read", db: "other-db" }]})
        ```
    1. Login to Other Database with Api/Reader user
        ```
        db.auth( { user: "fooApi", pwd: "aw3s0m3"});
        db.auth( { user: "fooDbReader", pwd: "u-guessed-it"});
1.  Backups/Exports
```
#!/bin/sh
mongoexport --host 127.0.0.1 -u fooDbReader -p "u-guessed-it" -d  other-db -c coll1 -o coll1.json
mongoexport --host 127.0.0.1 -u fooDbReader -p "u-guessed-it" -d  other-db -c coll2 -o coll2.json

tar -cvzf other-db-backup.tar.gz *.json
```
1.  Clear out
```
#!/bin/bash

mongo localhost:27017/other-db --quiet --authenticationDatabase admin -u fooApi -p 'aw3s0m3' --eval "db.coll1.remove({});"
mongo localhost:27017/other-db --quiet --authenticationDatabase admin -u fooApi -p 'aw3s0m3' --eval "db.coll2.remove({});"
```
1.  Imports
```
#!/bin/sh
mongoimport --host 127.0.0.1 --port 27018 -u fooApi -p aw3s0m3 -d  other-db -c coll1 --file coll1.json
mongoimport --host 127.0.0.1 --port 27018 -u fooApi -p aw3s0m3 -d  other-db -c coll2 --file coll2.json
```
