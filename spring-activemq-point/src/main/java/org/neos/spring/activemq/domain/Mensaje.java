package org.neos.spring.activemq.domain;

import java.io.Serializable;



public class Mensaje  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String mensaje;
	private String idQueue;
	
	public Mensaje(String mensaje,String idQueue){
		this.mensaje=mensaje;
		this.idQueue=idQueue;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getIdQueue() {
		return idQueue;
	}

	public void setIdQueue(String idQueue) {
		this.idQueue = idQueue;
	}
		

}
