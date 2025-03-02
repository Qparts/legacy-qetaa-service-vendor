package qetaa.service.vendor.helpers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Helper {
	
	
	private final static String SALTCHARS = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxuz1234567890";
	private final static String ACTIVATION_LINK = "http://localhost:8081/qvm/activation?code=";
	
	public static String getRandomSaltString(int length){
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
		return salt.toString();
	}
	
	public static int getRandomInteger(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}

	public static String getSecuredRandom() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	public static Date addMinutes(Date original, int minutes) {
		return new Date(original.getTime() + (1000L * 60 * minutes));
	}
	
	public String getDateFormat(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");
		return sdf.format(date);
	}

	public static String cypher(String text) throws NoSuchAlgorithmException {
		String shaval = "";
		MessageDigest algorithm = MessageDigest.getInstance("SHA-256");

		byte[] defaultBytes = text.getBytes();

		algorithm.reset();
		algorithm.update(defaultBytes);
		byte messageDigest[] = algorithm.digest();
		StringBuilder hexString = new StringBuilder();

		for (int i = 0; i < messageDigest.length; i++) {
			String hex = Integer.toHexString(0xFF & messageDigest[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		shaval = hexString.toString();

		return shaval;
	}
	
	public static Date addSeconds(Date original, int seconds) {
		return new Date(original.getTime() + (1000L * seconds));
	}
	
	public static String prepareHtmlActivationEmail(String activationCode, String companyName, int companyId) {
		String activationLink = ACTIVATION_LINK+activationCode + "&seq=" + companyId;
		String html = "<div align='cetnter' style='padding:12px'>" + 
				"        <table width='100%' border='0' cellpadding='0' cellspacing='0' style='font-family:helvetica'>" + 
				"            <tr>" + 
				"                <td>" + 
				"                    <table class='content' style='background-color:#f3f3f3' align='center' cellpadding='0' cellspacing='0' border='0'>" + 
				"                        <tr>" + 
				"                            <td>" + 
				"                                <div style='background-color:#555; color:white;padding:12px'>" + 
				"									QVM Account Activation" + 
				"								</div>" + 
				"                            </td>" + 
				"                        </tr>" + 
				"						<tr>" + 
				"                            <td>" + 
				"                                <div style='padding:12px'>" + 
				"									Your company "+companyName+" has been created!" + 
				"								</div>" + 
				"                            </td>" + 
				"                        </tr>" + 
				"						<tr>" + 
				"                            <td>" + 
				"                                <div style='padding:12px'>" + 
				"									Please activate your account by clicking on the link below" + 
				"								</div>" + 
				"                            </td>" + 
				"                        </tr>" + 
				"						<tr>" + 
				"                            <td>" + 
				"                                <div style='padding:12px' align='center'>" + 
				"									<a href='"+activationLink+"'>" + 
				"										<button  type='button' style='background-color:#ee4036; color:white;border:none;padding:12px'>Click here to activate your account</button>" + 
				"									</a>" + 
				"									<br/>" + 
				"									<br/>" + 
				"								</div>" + 
				"                            </td>" + 
				"                        </tr>" + 
				"						<tr>" + 
				"                            <td>" + 
				"                                <div style='padding:12px'>" + 
				"									Or copy and paste this link in your browser" + 
				"									<br/>" + 
				"									<a href='"+activationLink+"'>" + 
														activationLink +
				"									</a>" + 
				"									<br/>" + 
				"									<br/>" + 
				"								</div>" + 
				"                            </td>" + 
				"                        </tr>" + 
				"						<tr>" + 
				"                            <td>" + 
				"                                <div style='padding:12px'>" + 
				"									Welcome to QVM!" + 
				"									<br/>" + 
				"									<br/>" + 
				"								</div>" + 
				"                            </td>" + 
				"                        </tr>" + 
				"                    </table>" + 
				"                </td>" + 
				"            </tr>" + 
				"        </table>" + 
				"		</div>";
		return html;
	}

}