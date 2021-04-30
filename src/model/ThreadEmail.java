package model;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import model.dao.DAOEmail;

import org.apache.log4j.Logger;

import util.MyCriptografia;
import util.SendEmail;

import entidade.Email;

/**
 * fica lendo a tabela de emails, e entao enviar 1 a 1
 */
public class ThreadEmail extends Thread{	
	
	private Logger logger = Logger.getLogger(ThreadEmail.class);
	
	private String html = "";
	
	public ThreadEmail() throws Exception {
		this.html = carregarHtml();
	}
	
	public void run() {
		while(true){
			try{
				logger.debug("inicio loop thread email");
				DAOEmail dao = new DAOEmail();
				Email e = null;
				//fica lendo as mensagens do banco de dados
				while((e = dao.getEmailEnviar())!=null){
					try{
						String newHtml = html.replaceFirst("55content", e.getMensagem());
						newHtml = newHtml.replaceFirst("55header", e.getTitulo());
						sendEmail(e.getDestinatario(), e.getDestinatario(), newHtml);
						dao.atualizar(e.getId(), true);
					}catch (Exception ex) {
						logger.debug("erro ao enviar email:"+e.getId(), ex);
						dao.atualizar(e.getId(), false);
					}
				}
				logger.debug("fim loop thread email");
				Thread.sleep(Configuracoes.getIntValue("delay_sms"));
			}catch (Throwable e) {
				logger.debug(e);
				try {
					Thread.sleep(Configuracoes.getIntValue("delay_sms"));
				} catch (InterruptedException e1) {
					logger.debug(e);
				}
			}	
		}
	
	}
	
	/**
	 * carrega o html do corpo do email
	 * @return
	 * @throws Exception
	 */
	private String carregarHtml() throws Exception{
			String html = "";
			Scanner scanner = new Scanner(getClass().getResourceAsStream("/email.html"));
			try {
				while (scanner.hasNextLine()) {
					try {
						html += scanner.nextLine();
					} catch (Exception e) {
						e.printStackTrace();
						logger.debug(e);
						break;
					}
				}
			} finally {
				scanner.close();
			}
			
			return html;
	}
	
	
	
	private void sendEmail(String to, String tittle, String body)
	throws Exception {
		
		ArrayList<String> toStr = new ArrayList<String>();
		
		String from = Configuracoes.getValue("from_email");
		//String host = Configuracoes.getValue("host_email");
		String senha = MyCriptografia.descrypito(Configuracoes.getValue("fuck_email"));
		
		toStr.add(to);
		
		SendEmail sm = new SendEmail();
		
		sm.sendMail(from,senha,toStr.toArray(),tittle,body);
		
	}
	
	/**
	 * apenas envia o email
	 * @param to
	 * @param tittle
	 * @param body
	 * @throws Exception
	 */
	//	private void sendEmail(String to, String tittle, String body)
	//			throws Exception {
	//		// Sender's email ID needs to be mentioned
	//		String from = Configuracoes.getValue("from_email");
	//		// Assuming you are sending email from localhost
	//		String host = Configuracoes.getValue("host_email");
	//		// Get system properties
	//		Properties properties = System.getProperties();
	//		// Setup mail server
	//		properties.setProperty("mail.smtp.host", host);
	//		// Get the default Session object.
	//		Session session = Session.getDefaultInstance(properties);
	//		// Create a default MimeMessage object.
	//		MimeMessage message = new MimeMessage(session);
	//		// Set From: header field of the header.
	//		message.setFrom(new InternetAddress(from,Configuracoes.getValue("titulo_email")));
	//		// Set To: header field of the header.
	//		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	//		// Set Subject: header field
	//		message.setSubject(tittle);
	//		// Now set the actual message
	//		message.setContent(body, "text/html");
	//		// Send message
	//		Transport.send(message);
	//		
	//	}
	 
}
