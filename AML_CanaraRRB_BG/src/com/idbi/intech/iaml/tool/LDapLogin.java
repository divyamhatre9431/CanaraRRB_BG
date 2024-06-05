package com.idbi.intech.iaml.tool;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class LDapLogin {
	public static void main(String args[]){
		try {
			Hashtable<String, String> env;
			env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, "ldap://ldap.idbibank.com/");
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, "uid=" + "anup_salastekar"
					+ ", o=idbi, c=IN");
			env.put(Context.SECURITY_CREDENTIALS, "Anup@198");
			LdapContext ctx = new InitialLdapContext(env, null);
		} catch (AuthenticationException e) {
			System.out.println("Wrong password......");
		} catch (Exception e) {
			System.out.println("AD / LDAP bind error: " + e);
		}
		System.out.println("Done... ");
	}

}
