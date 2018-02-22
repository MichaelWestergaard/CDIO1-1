package dal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import dto.UserDTO;

public class userDAO implements IUserDAO {
	private List<UserDTO> users = new ArrayList<UserDTO>();

	public UserDTO getUser(int userId) throws DALException {	
		for(int i = 0; i < users.size(); i++) {
			if(users.get(i).getUserId()==userId) {
				return users.get(i);
			}
		}
		return null;
	}

	public List<UserDTO> getUserList() throws DALException, IOException, ClassNotFoundException {
		FileInputStream fi = new FileInputStream(new File("users.data"));
		ObjectInputStream oi = new ObjectInputStream(fi);

		// Read objects
		users = (List<UserDTO>) oi.readObject();
		oi.close();
		fi.close();
		return users;
	}

	public void saveUsers() throws IOException {
		FileOutputStream f = new FileOutputStream(new File("users.data"));
		ObjectOutputStream o = new ObjectOutputStream(f);

		// Write objects to file
		o.writeObject(users);

		o.close();
		f.close();
	}

	public void createUser(int userID, String userName, String ini, String role, String cpr) throws DALException, IOException {
		if(getUser(userID) == null) {
			String password = makePassword(10);
			users.add(new UserDTO(userID, userName, ini, role, cpr, password));
			saveUsers();
		}
	}

	public boolean checkPassword(String password) {

		char [] passwordArray = new char[password.length()];
		for (int i = 0; i < password.length(); i ++) {
			passwordArray[i] = password.charAt(i);
		}
		int counter = 0;
		boolean little = false, big = false, number = false, special = false;
		if (password.length()>=6) {
			for (int j = 0; j < passwordArray.length; j++) {
				if (passwordArray[j]>='A' && passwordArray[j]<='Z' && !big) {
					counter++;
					big = true;
				} else if (passwordArray[j]>='a' && passwordArray[j]<='z' && !little) {
					counter++;
					little = true;
				} else if (passwordArray[j]>='0' && passwordArray[j]<='9' && !number) {
					counter++;
					number = true;
				} else if (passwordArray[j]=='.' || passwordArray[j] =='-' || passwordArray[j] =='_' 
						|| passwordArray[j] == '+' || passwordArray[j] == '!' || passwordArray[j] == '?' || passwordArray[j] == '=' && !special) {
					counter++;
					special = true;
				}
			}
		}
		if (counter >= 3) {
			return true;

		} else {
			return false;
		}
	}

	public boolean checkUserName(String userName) {
		if(userName.length() >= 2 && userName.length() <= 20) {
			for(int i = 0; i < users.size();i++) {
				if(users.get(i).getUserName().equals(userName)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	public boolean checkIni(String ini) {
		if(ini.length() >= 2 && ini.length() <= 4) {
			for(int i = 0; i < users.size(); i++) {
				if(users.get(i).getIni().equals(ini)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	public boolean checkRole(String role) {
		if (role.toLowerCase().equals("admin") || role.equals("pharmacist") || role.equals("foreman") || role.equals("operator")) {
			return true;
		}
		return false;
	}

	public boolean checkCPR(String cpr) {
		
		if(cpr.length() == 11) {
			String[] splitCPR = cpr.split("-");
			String combinedCPR = splitCPR[0] + splitCPR[1];
			char[] cprChars = combinedCPR.toCharArray();
			int cprDay = Integer.parseInt(new StringBuilder().append(cprChars[0]).append(cprChars[1]).toString());
			int cprMonth = Integer.parseInt(new StringBuilder().append(cprChars[2]).append(cprChars[3]).toString());
			int cprYear = Integer.parseInt(new StringBuilder().append(cprChars[4]).append(cprChars[5]).toString());
			boolean cprDublicate = false;
			
			if(cprDay > 0 && cprDay < 32 && cprMonth > 0 && cprMonth < 13 && cprYear >= 0 && cprYear <= 99) {
				for(int i = 0; i < users.size(); i++) {
					if(users.get(i).getCpr() == cpr) {
						cprDublicate = true;
						break;
					}
				}
				
				if(!cprDublicate) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkId(int userId) {
		if(userId >= 11 && userId <= 99) {
			for(int i = 0; i < users.size(); i++) {
				if(users.get(i).getUserId() != userId) {
					return true;
				}
			}
		}
		return false;
	}

	public void updateUserName(int userId, String userName) throws DALException { //Disse burde tag en int Id parameter? idk bg
		for(int i = 0; i < users.size(); i++) {
			if(users.get(i).getUserId()==userId) {
				userName = users.get(i).getUserName();
			}
		}
	}

	public void updateIni(int userId, String ini) throws DALException {
		for(int i = 0; i < users.size(); i++) {
			if(users.get(i).getUserId()==userId) {
				ini = users.get(i).getIni();
			}
		}
	}

	public void updateRole(int userId, String role) throws DALException {
		for(int i = 0; i < users.size(); i++) {
			if(users.get(i).getUserId()==userId) {
				role = users.get(i).getIni();
			}
		}
	}

	public void updateCpr(int userId, String cpr ) throws DALException {
		for(int i = 0; i < users.size(); i++) {
			if(users.get(i).getUserId()==userId) {
				cpr = users.get(i).getIni();
			}
		}
	}


	/* Adgangskoden skal indeholde mindst 6 tegn af mindst tre af de følgende fire kategorier: små bogstaver (’a’ til ’z’), store bogstaver (’A’ til ’Z’), cifre (’0’ til ’9’) og specialtegn (som defineret herunder).
	    Undgå at bruge dit fornavn, efternavn eller bruger-ID som en del af din adgangskode, da dette vil medføre problemer med at logge ind på nogle systemer og tjenester på DTU, især Windows-tjenester.
	    Anvend blot følgende special-tegn: {'.', '-', '_', '+', '!', '?', '='} */
	public static String makePassword(int length){
		String password = "";

		for(int i = 0; i < length - 2 ; i++) {
			password = password + randomCharacter("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

		}
		String randomDigit = randomCharacter("0123456789");
		password = insertAtRandom(password, randomDigit);
		String randomSymbol = randomCharacter(".-_+!?=");
		password = insertAtRandom(password, randomSymbol);
		return password;
	}

	public static String randomCharacter(String characters) {
		int n = characters.length();
		int r = (int) (n*Math.random());
		return characters.substring(r,r + 1);
	}

	public static String insertAtRandom(String str, String toInsert) {
		int n = str.length();
		int r = (int) ((n+1) * Math.random());
		return str.substring(0,r) + toInsert + str.substring(r);

	}





	public void updatePassword() throws DALException {

	}

	public void deleteUser(int userId) throws DALException, IOException {
		if(users.contains(getUser(userId))) {			//Bør man også tjekke brugerens 'role'? ''
			users.remove(getUser(userId));
			saveUsers();
		}
	}

	public void updateUser(UserDTO user) throws DALException {
		// TODO Auto-generated method stub

	}

}