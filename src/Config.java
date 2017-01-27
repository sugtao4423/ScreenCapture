import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config{

	private File propFile;
	private Properties p;

	private String ck, cs, at, ats, saveDir;

	public Config(){
		String dirPath = new File(System.getProperty("java.class.path")).getParent();
		propFile = new File(dirPath + "/ScreenCapture.conf");
		try{
			if(!propFile.exists())
				propFile.createNewFile();
			p = new Properties();
			FileInputStream fis = new FileInputStream(propFile);
			p.load(fis);
			fis.close();
		}catch(IOException e){
		}
	}

	public void loadTwitter(){
		ck = Keys.ck;
		cs = Keys.cs;
		at = p.getProperty("AT", "");
		ats = p.getProperty("ATS", "");
	}

	public void loadConfig(){
		saveDir = p.getProperty("saveDir", "");
	}

	public String[] getTwitter(){
		return new String[]{ck, cs, at, ats};
	}

	public String getSaveDir(){
		return saveDir;
	}

	public void addProperties(String key, String value){
		String at = p.getProperty("AT", "");
		String ats = p.getProperty("ATS", "");
		String saveDir = p.getProperty("saveDir", "");

		switch(key){
		case "AT":
			at = value;
			break;
		case "ATS":
			ats = value;
			break;
		case "saveDir":
			saveDir = value;
			break;
		}

		p = new Properties();
		p.setProperty("AT", at);
		p.setProperty("ATS", ats);
		p.setProperty("saveDir", saveDir);

		this.at = at;
		this.ats = ats;
		this.saveDir = saveDir;

		try{
			FileOutputStream fos = new FileOutputStream(propFile);
			p.store(fos, null);
			fos.close();
		}catch(IOException e){
		}
	}
}
