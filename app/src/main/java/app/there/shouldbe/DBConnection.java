package app.there.shouldbe;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnection extends SQLiteOpenHelper {

	final static int DB_VERSION = 1;
	final static String DB_NAME = "mydb.shouldbedb";
	// SHOULDBEINFOTABLE 
	final static String SHOULDBEINFOTABLE = "SHOULDBEINFOTABLE";
	final static String SHOULDBE_ID_COLUMN = "ID";
	final static String SHOULDBE_TEXT_COLUMN = "SHOULDBE";
	
	//LIKETABLE
	final static String LIKETABLE = "LIKETABLE";
	final static String LIKE_ID_COLUMN = "ID";
	final static String LIKE_SHOULDBE_COLUMN = "FK_SHOULDBE_ID";
	
	Context context;
	
	public DBConnection(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		executeSQLScript(db, "ShouldBe/create.sql");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	private void executeSQLScript(SQLiteDatabase db, String dbname) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
		int len;
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		
		try {
			inputStream = assetManager.open(dbname);
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			outputStream.close();
			inputStream.close();
			String[] createScript = outputStream.toString().split(";");
			
			for (int i = 0; i < createScript.length; i++) {
				String sqlStatement = createScript[i].trim();
				if (sqlStatement.length() > 0) {
					db.execSQL(sqlStatement + ";");
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	

}
