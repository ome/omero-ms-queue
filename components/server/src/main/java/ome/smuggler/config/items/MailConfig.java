package ome.smuggler.config.items;

import java.util.Arrays;
import java.util.Objects;

/**
 * Specifies how to configure the mail service.
 * The available settings:
 * <ul>
 *  <li>{@link #setMailServerHost(String) Mail server host}. The host name of 
 *  the machine where the mail server runs. Mail will be sent to this server.  
 *  </li>
 *  <li>{@link #setMailServerPort(int) Mail server port}. The port of the mail
 *  server given above.
 *  </li>
 *  <li>{@link #setUseSmtps(boolean) Secure connection}. If {@code true} then
 *  use SMTPS (i.e. SMTP over TLS/SSL), otherwise plain, unprotected SMTP.
 *  If not specified, it defaults to {@code false} and plain SMTP will be used.
 *  </li>
 *  <li>{@link #setSkipServerCertificateValidation(boolean) Skip server 
 *  certificate validation}. If set to {@code true} when using an SMTPS 
 *  connection, the mail server certificate is not checked for validity.
 *  Ideally this setting should never be used (security loophole!) but it may be
 *  your only option with a self-signed server certificate which you cannot add
 *  to the Java key store. This setting defaults to {@code false} if not 
 *  specified and will be in any case ignored for an SMTP connection.  
 *  </li>
 *  <li>{@link #setUsername(String) Username}. The username to log into the
 *  mail server, if required. Leave blank if not needed.
 *  </li>
 *  <li>{@link #setPassword(String) Password}. The password to log into the
 *  mail server, if required. Leave blank if not needed.
 *  </li>
 *  <li>{@link MailConfig#setFromAddress(String) From address}. The "from" 
 *  address of each and every mail we send.
 *  </li>
 *  <li>{@link #setRetryIntervals(Long[]) Retry intervals}.
 *  How many times and at which intervals to retry a failed mail relay.
 *  If a relay fails, it will be retried after the number of minutes given in
 *  the first slot of this array; if the retry fails, it will be attempted again
 *  after the number of minutes given in the second slot of this array; and so
 *  on until the relay succeeds or the last slot is used. So a failed relay
 *  will be retried at most a number of times equal to the length of this array;
 *  if the array is {@code null} or empty, no retries will ever be attempted.
 *  </li>
 *  <li>{@link #setDeadMailDir(String) Dead mail directory}.
 *  Path to the directory where to store email messages that could not be 
 *  relayed to the mail server, that is, messages whose delivery failed past
 *  the configured number of retries as explained above. 
 *  A message in the dead mail directory will be kept indefinitely, the system 
 *  administrator will have to explicitly delete it after re-sending it to the
 *  intended recipient, for example by inputting the file into a program such
 *  as {@code sendmail}.
 *  Note that directories in the given path will be created if needed.
 *  </li>
 *  <li>{@link #setSysAdminEmail(String) System administrator's email}.
 *  Optional email address of the system administrator. If specified, every time
 *  an import fails permanently an alert email is sent to this address. 
 *  </li>
 * </ul>
 */
public class MailConfig {
    // NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
    // be (de-)serialized painlessly by SnakeYaml.
    
    private String fromAddress;
    private String mailServerHost;
    private int mailServerPort;
    private boolean useSmtps;
    private boolean skipServerCertificateValidation;
    private String username;
    private String password;
    private Long[] retryIntervals;
    private String deadMailDir;
    private String sysAdminEmail;
    
    public String getMailServerHost() {
        return mailServerHost;
    }
    
    public void setMailServerHost(String host) {
        this.mailServerHost = host;
    }
    
    public int getMailServerPort() {
        return mailServerPort;
    }
    
    public void setMailServerPort(int port) {
        this.mailServerPort = port;
    }
    
    public boolean getUseSmtps() {
        return useSmtps;
    }

    public void setUseSmtps(boolean useSmtps) {
        this.useSmtps = useSmtps;
    }
    
    public boolean getSkipServerCertificateValidation() {
        return skipServerCertificateValidation;
    }

    public void setSkipServerCertificateValidation(boolean skipServerCertificateValidation) {
        this.skipServerCertificateValidation = skipServerCertificateValidation;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    
    public Long[] getRetryIntervals() {
        return retryIntervals;
    }

    public void setRetryIntervals(Long[] retryIntervals) {
        this.retryIntervals = retryIntervals;
    }

    public String getDeadMailDir() {
        return deadMailDir;
    }

    public void setDeadMailDir(String failedRelayDir) {
        this.deadMailDir = failedRelayDir;
    }
    
    public String getSysAdminEmail() {
        return sysAdminEmail;
    }

    public void setSysAdminEmail(String sysAdminEmail) {
        this.sysAdminEmail = sysAdminEmail;
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MailConfig) {
            return Objects.equals(other.toString(), this.toString());
        }
        return false;
    }
    
    @Override
    public String toString() {
        String xs = Arrays.toString(retryIntervals);
        return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s | %s", 
                             mailServerHost, mailServerPort, useSmtps, 
                             skipServerCertificateValidation ,username,
                             password, fromAddress, deadMailDir, sysAdminEmail,
                             xs);
    }
    
}
