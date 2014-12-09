package com.example.androidstuido_app_example.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author Gaurav Gupta <gaurav@thegauravgupta.com>
 * @since 09/Dec/2014
 */

public class EmailGmail extends AsyncTask<Void, Void, Void> {

    String mSubject;
    String mEmailBody;
    String mRecipientEmail;
    String mSenderEmail;
    String mSenderPassword;

    /**
     * Just pass in the required parameters to send the email
     * <p/>Eg: EmailGmail email = new EmailGmail("sender@gmail.com", "password","App email", "Hello this is a sample message", "receipent@gmail.com" );
     * email.execute();
     * <p/>Dependency: Three libraries have to be added to libs folder namely: <a href="https://code.google.com/p/javamail-android/downloads/list">additionnal.jar, mail.jar, activation.jar</a>
     * <p/>Permission: android.permission.INTERNET
     *
     * @param senderEmail    Sender's Email
     * @param senderPassword Sender's Password
     * @param subject        Subject
     * @param emailBody      Email body
     * @param recipientEmail Recipient's Email
     */
    public EmailGmail(String senderEmail, String senderPassword, String subject, String emailBody, String recipientEmail) {
        this.mSenderEmail = senderEmail;
        this.mSenderPassword = senderPassword;
        this.mSubject = subject;
        this.mEmailBody = emailBody;
        this.mRecipientEmail = recipientEmail;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Log.i(Constant.NAV_TAG, "Class:" + ((Object) this).getClass().getSimpleName() + " Method:doInBackground");

            EmailSender emailObjClient = new EmailSender(mSenderEmail, mSenderPassword);

            try {
                boolean sendingclient = emailObjClient.sendMail(mSubject, mEmailBody, mSenderEmail, mRecipientEmail);
                if (!sendingclient) {
                    throw new Exception("error sending email");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.i(Constant.NAV_TAG, "Class:" + ((Object) this).getClass().getSimpleName() + " Method:onPostExecute");
        super.onPostExecute(result);
    }

}

class EmailSender extends Authenticator {

    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public EmailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized boolean sendMail(String subject, String body, String sender, String recipients) throws Exception {
        try {
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            }
            else {
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            }
            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null) {
                return "application/octet-stream";
            }
            else {
                return type;
            }
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}


class JSSEProvider extends Provider {

    public JSSEProvider() {
        super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
        AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {
            public Void run() {
                put("SSLContext.TLS",
                        "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
                put("Alg.Alias.SSLContext.TLSv1", "TLS");
                put("KeyManagerFactory.X509",
                        "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
                put("TrustManagerFactory.X509",
                        "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
                return null;
            }
        });
    }
}
