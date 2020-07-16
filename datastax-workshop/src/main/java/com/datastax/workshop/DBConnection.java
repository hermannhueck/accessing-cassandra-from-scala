package com.datastax.workshop;

/*
 * EXERCISE 2:
 *  
 * DONE: Please change this constants with the values 
 * you used when you created the ASTRA instance.
 */
public interface DBConnection {

    // This is the Zip file you downloaded
    String SECURE_CONNECT_BUNDLE = "secure-connect-killrvideocluster.zip";

    // This is the username, recommended value was KVUser
    String USERNAME = "KVUser";

    // This is the password, recommended value was KVPassword
    String PASSWORD = "KVPassword";

    // This is the keyspace name, recommended value was killrvideo
    String KEYSPACE = "killrvideo";

}