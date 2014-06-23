package com.aga.mine.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.android.vending.expansion.zipfile.ZipResourceFile.ZipEntryRO;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class ExtractResourceActivity extends Activity{

	private ProgressDialog mDialog;
	private static final String TAG = ExtractResourceActivity.class.getSimpleName();
	
	private static final String DATA_PATH = "/Android/data/";
	private static final String OBB_PATH = "/Android/obb/";
	private static final String FOLDER_EXTRACT = "com.aga.mine.main.resources";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		File mFolderExtractPath = new File(Environment.getExternalStorageDirectory() + DATA_PATH + FOLDER_EXTRACT);
		if(mFolderExtractPath.exists()){
			Log.e(TAG, "Folder extract exists");
			startActivity(new Intent(ExtractResourceActivity.this, MainActivity.class));
		}else{
			Log.e(TAG, "Folder extract not exists");
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        setContentView(R.layout.activity_extract_resource);
	        //Call task extract
	        new ExtractResourceTask().execute();
		}
		
	}
	
	
	private class ExtractResourceTask extends AsyncTask<Void, Void, Void>{

		String mPackageName = getApplicationContext().getPackageName();
		File mRootPath = Environment.getExternalStorageDirectory();
		File mObbFullPath = new File(mRootPath + OBB_PATH + mPackageName);
		
		private void showDialog(String title, String message){
			mDialog = ProgressDialog.show(ExtractResourceActivity.this, title, message, true);
		}
				
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showDialog("Please wait", "Extracting resources ...");
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			extract();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			startActivity(new Intent(ExtractResourceActivity.this, MainActivity.class));
		}
		
		private void extract(){
			if(mObbFullPath.exists()){
				String mObbFilePath = null;
				String mPathToExtract = null;
				try {
					mObbFilePath = mObbFullPath.toString() + File.separator 
												+ "main."
												+ getApplicationContext().getPackageManager().getPackageInfo(mPackageName, 0).versionCode
												+ "."
												+ mPackageName
												+ ".obb";
					Log.e(TAG, "Obb file path: " + mObbFilePath);
					
					ZipResourceFile mExpansionFile = new ZipResourceFile(mObbFilePath);
					ZipEntryRO[] mZip = mExpansionFile.getAllEntries();
					Log.e(TAG, "zip[0].isUncompressed() : " + mZip[0].isUncompressed());
	                Log.e(TAG, "mFile.getAbsolutePath() : " + mZip[0].mFile.getAbsolutePath());
	                Log.e(TAG, "mFileName : " + mZip[0].mFileName);
	                Log.e(TAG, "mZipFileName : " + mZip[0].mZipFileName);
	                Log.e(TAG, "mCompressedLength : " + mZip[0].mCompressedLength);
					
					mPathToExtract = mRootPath + DATA_PATH + FOLDER_EXTRACT;
					Log.e(TAG, "Path to extract: " + mPathToExtract);
					
					extractZip(mObbFilePath, mPathToExtract);
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		
		private void extractZip(String pathOfZip,String pathToExtract)
		{
		        int BUFFER_SIZE = 4096;//1024
		        int size;
		        byte[] buffer = new byte[BUFFER_SIZE];
		        try {
		            File f = new File(pathToExtract);
		            if(!f.exists()){
		            	boolean success = f.mkdirs();
		            	if(success){
		            		Log.e(TAG, "Create folder extract successed");
		            	}else{
		            		Log.e(TAG, "Create folder extract failed");
		            	}
		            }
//		            if(!f.isDirectory()) {
//		                f.mkdirs();
//		            }
		            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(pathOfZip), BUFFER_SIZE));
		            try {
		                ZipEntry ze = null;
		                while ((ze = zin.getNextEntry()) != null) {
		                    String path = pathToExtract  +"/"+ ze.getName();

		                    if (ze.isDirectory()) {
		                        File unzipFile = new File(path);
		                        if(!unzipFile.isDirectory()) {
		                            unzipFile.mkdirs();
		                        }
		                    }
		                    else {
		                        FileOutputStream out = new FileOutputStream(path, false);
		                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
		                        try {
		                            while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
		                                fout.write(buffer, 0, size);
		                            }

		                            zin.closeEntry();
		                        }catch (Exception e) {
		                            Log.e("Exception", "Unzip exception 1:" + e.toString());
		                        }
		                        finally {
		                            fout.flush();
		                            fout.close();
		                        }
		                    }
		                }
		            }catch (Exception e) {
		                Log.e("Exception", "Unzip exception2 :" + e.toString());
		            }
		            finally {
		                zin.close();
		            }
		            //return true;
		        }
		        catch (Exception e) {
		            Log.e("Exception", "Unzip exception :" + e.toString());
		        }
		        //return false;
		    }
	}
	
}
