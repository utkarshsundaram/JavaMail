package com.example.user.sendingemail.Asyntask;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.example.user.sendingemail.Utilities.Configuration;
import com.example.user.sendingemail.Utilities.Constants;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class MyAsynTask extends AsyncTask<Void,Void,Void>
{
    private Context context;
    private Session session;
    private String email;
    private String message;
    private String name;
    private String attchedFiles;
    private ProgressDialog progressDialog;
    public MyAsynTask(Context context, String email,String message,String name,String attchedFiles)
    {
        this.context = context;
        this.email = email;
        this.message = message;
        this.name=name;
        this.attchedFiles=attchedFiles;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);



    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        Toast.makeText(context,"Message Sent", Toast.LENGTH_LONG).show();

    }

    @Override
    protected Void doInBackground(Void... params)
    {
        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session

        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Configuration.EMAIL, Configuration.PASSWORD);
                    }
                });
        try
        {
            //Creating MimeMessage object

            MimeMessage messageToSend = new MimeMessage(session);
            messageToSend.setFrom(new InternetAddress(Configuration.EMAIL));
            //Setting sender address
            messageToSend.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
          /*  if(receipentEmail !=null)
            {
                mm.addRecipient(Message.RecipientType.CC,new InternetAddress(receipentEmail));
            }
           // mm.addRecipient(Message.RecipientType.CC,new InternetAddress("amar.tyagi@kelltontech.com"));
*/
            messageToSend.setSubject("password");
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(Constants.Dear+name+Constants.Congratulation+Constants.InfoPassword+message);

            // adding body
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            if(attchedFiles!=null)
            {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attchedFiles);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(attchedFiles);
                multipart.addBodyPart(messageBodyPart);
                // Send the complete message parts
                messageToSend.setContent(multipart);
                // Send message
                Transport.send(messageToSend);
            }
            else
                {
                // Send the complete message parts
                messageToSend.setContent(multipart);
                // Send message
                Transport.send(messageToSend);
            }

        }
        catch (MessagingException e)
        {
            Log.e("MYAPP", "exception", e);

        }
        return null;
    }
}
