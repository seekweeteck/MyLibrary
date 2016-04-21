package mmsr.stat;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION =2;
	private static final String DATABASE_NAME = "readStat";
	private static final String TABLE_NAME = "stat";
	private static final String TABLE_FEEDBACK = "feed";

	// Table Columns names
	// KEY_ID, KEY_DATE, KEY_TIME, KEY_EN, KEY_BM, KEY_CN, KEY_TM;

	private static final String KEY_ID = "r_id";
	private static final String KEY_DATE = "r_date";
	private static final String KEY_TIME = "r_time";
	private static final String KEY_EN = "r_en";
	private static final String KEY_BM = "r_bm";
	private static final String KEY_CN = "r_cn";
	private static final String KEY_TM = "r_tm";

	// Table columns feedback
	private static final String FEED_ID = "f_id";
	private static final String FEED_RATE = "f_rate";
	private static final String FEED_PAR = "f_par";
	private static final String FEED_SIB = "f_sib";
	private static final String FEED_OTH = "f_oth";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DatabaseHandler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE_FEED = "CREATE TABLE " + TABLE_FEEDBACK + "("
				+ FEED_ID + " INTEGER PRIMARY KEY," + FEED_RATE + " REAL,"
				+ FEED_PAR + " INTEGER," + FEED_SIB + " INTEGER," + FEED_OTH
				+ " INTEGER)";

		db.execSQL(CREATE_TABLE_FEED);

		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT," + KEY_TIME
				+ " TEXT," + KEY_EN + " INTEGER," + KEY_BM + " INTEGER,"
				+ KEY_CN + " INTEGER," + KEY_TM + " INTEGER)";

		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
		onCreate(db);

	}
	
	

	public void addRecord(ReadRecord rec) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_ID, rec.getR_id());
		values.put(KEY_DATE, rec.getR_date());
		values.put(KEY_TIME, rec.getR_time());
		values.put(KEY_EN, rec.getR_en());
		values.put(KEY_BM, rec.getR_bm());
		values.put(KEY_CN, rec.getR_cn());
		values.put(KEY_TM, rec.getR_tm());

		// Inserting Row
		db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection
	}

	public void addFeedRecord(FeedbackRecord rec) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(FEED_ID, rec.getId());
		values.put(FEED_RATE, rec.getRating());
		values.put(FEED_PAR, rec.getParent());
		values.put(FEED_SIB, rec.getSibling());
		values.put(FEED_OTH, rec.getOthers());

		// Inserting Row
		db.insert(TABLE_FEEDBACK, null, values);
		db.close(); // Closing database connection
	}

	public ReadRecord getContact(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID, KEY_DATE,
				KEY_TIME, KEY_EN, KEY_BM, KEY_CN, KEY_TM }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		ReadRecord rec = new ReadRecord();
		db.close();
		return rec;
	}

	public FeedbackRecord getFeedback(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_FEEDBACK, new String[] { FEED_ID,
				FEED_RATE, FEED_PAR, FEED_SIB, FEED_OTH }, FEED_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		FeedbackRecord rec = new FeedbackRecord();
		
		if(cursor.getCount()>=1){
			rec.setId(id);
			rec.setRating(Float.parseFloat(cursor.getString(1)));
			rec.setParent(cursor.getInt(2));
			rec.setSibling(cursor.getInt(3));
			rec.setOthers(cursor.getInt(4));
		}
		
		db.close();
		return rec;
	}

	public List<ReadRecord> getAllContacts() {
		List<ReadRecord> recList = new ArrayList<ReadRecord>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ReadRecord rec = new ReadRecord();

				rec.setR_id(Integer.parseInt(cursor.getString(0)));
				rec.setR_date(cursor.getString(1));
				rec.setR_time(cursor.getString(2));
				rec.setR_en(Integer.parseInt(cursor.getString(3)));
				rec.setR_bm(Integer.parseInt(cursor.getString(4)));
				rec.setR_cn(Integer.parseInt(cursor.getString(5)));
				rec.setR_tm(Integer.parseInt(cursor.getString(6)));
				recList.add(rec);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return recList;
	}

	public int updateContact(ReadRecord rec) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_EN, rec.getR_en());
		values.put(KEY_BM, rec.getR_bm());
		values.put(KEY_CN, rec.getR_cn());
		values.put(KEY_TM, rec.getR_tm());

		// updating row
		return db.update(
				TABLE_NAME,
				values,
				KEY_ID + " = ? AND " + KEY_DATE + " = ?",
				new String[] { String.valueOf(rec.getR_id()),
						String.valueOf(rec.getR_date()) });
	}
	
	public int updateFeedback(FeedbackRecord rec) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(FEED_RATE, rec.getRating());
		values.put(FEED_PAR, rec.getParent());
		values.put(FEED_SIB, rec.getSibling());
		values.put(FEED_OTH, rec.getOthers());

		// updating row
		return db.update(
				TABLE_FEEDBACK,
				values,
				FEED_ID + " = ?",
				new String[] { String.valueOf(rec.getId())});
	}

	public void deleteContact(ReadRecord rec) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, KEY_ID + " = ?",
				new String[] { String.valueOf(rec.getR_id()) });
		db.close();
	}

	public void deleteAllContact() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}

	public void deleteFeedback(int ID) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FEEDBACK, FEED_ID + " = ?",
				new String[] { String.valueOf(ID) });
		db.close();
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		super.onDowngrade(db, oldVersion, newVersion);
	}
}
