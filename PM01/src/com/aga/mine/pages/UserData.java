package com.aga.mine.pages;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;

import com.facebook.model.GraphUser;

import android.content.Context;
import android.util.Log;

//�ϴ� �Ϸ�(�׽�Ʈ ��)
public class UserData{

	public static String userID = "";
	public static String userName = "Guest";
	public static  GraphUser facebookUserInfo = null;
	public static String accessToken = "";
	
//	public static ArrayList<String> facebookFriends = new ArrayList<String>();
	public static  List<GraphUser> facebookFriendsInfo = new ArrayList<GraphUser>();
	private int kResultWin = 1;
	private int kResultLose = 0;
	
	int kOptionBGM = 0;
	int kOptionSFX = 1;
	int kOptionRefuseInvitation = 2;
	int kOptionReceiveBloomstick = 3;
	int kOptionShowMyPicture = 4;
	int kOptionNoviceGuide = 5;
	public int broomtime = 0;
	
	public static int difficulty = 0; // ���� ���̵�
	
	//
	// �ִ� �⺻ ���� ���ڷ� ��
	final int timeMaxBroomstick = 6; // 6��
	// Ȩ�� ������ �� �ִ� �ִ� ���ڷ� ��
	final int homeMaxBroomstick = 20; // 20��
	// �ִ� ���� ��
	final int postboxMax = 30; 
	
	// �⺻ ���� ���ڷ� ���� �ð�(15��)
	public final long broomstickRenewalTime = 900000L; // �⺻ 900000L
//	public final long broomstickRenewalTime = 20000L;
	// ���ڷ� ������ Ȱ�� �ð�(3�ð�) (��뿡�� ���� ���ڷ�1 to������)
	public final long broomstickRepresentTime = 10800000L;
	// ģ�� �ʴ� Ȱ�� �ð� (24�ð�) (������ ���ڷ�1)
	public final long friendReinviteTime = 86400000L;
	
	// �ʴ� ������ ���� (���� : gold)
	public final int invite30 = 5000;
	public final int invite60 = 10000;
	public final int invite90 = 30000;
	
	// ���� �⼮ ���� (�ִ� 30��)
	final public int[] rollBook = {
			100,200,300,400,1000,
			500,600,700,800,2000,
			900,1000,1100,1200,3000,
			1300,1400,1500,1600,4000,
			1700,1800,1900,2000,5000,
			2100,2200,2300,2400,10000};
	
	// ���ڷ� ���� ���
	private HashMap<Integer, Integer> buyBroomstick = new HashMap<Integer, Integer>();
	private void bBroomstick() {
		buyBroomstick.put(5, 1000);
		buyBroomstick.put(10, 1800);
		buyBroomstick.put(20, 3400);
		buyBroomstick.put(40, 6400);
		buyBroomstick.put(80, 12000);
		buyBroomstick.put(160, 22400);
	}
	
	// ��� ���� ���
	private HashMap<Integer, String> buyGold = new HashMap<Integer, String>();
	private void bGold() {
		buyGold.put(5000, "$0.99");
		buyGold.put((int)(25000*1.25f), "$4.99");
		buyGold.put((int)(50000*1.30f), "$9.99");
		buyGold.put((int)(150000*1.35f), "$24.99");
		buyGold.put((int)(250000*1.40f), "$49.99");
		buyGold.put((int)(500000*1.45f), "$99.99");
	}
	
	// ������ ���� ���
	final public int[] buySphere = {
			2000, 4600, 10500, 24100, 55400, 127400};
	
	final public int[][] magicPrice = {
			{ 800, 1040, 1350, 1750, 2270, 2950, 3830, 4970, 6460, 8390, 10900,
					14170, 18420, 23940, 31120, 40450, 52580, 68350, 88850, 115500 },
			{ 560, 720, 930, 1200, 1560, 2020, 2620, 3400, 4420, 5740, 7460,
					9690, 12590, 16360, 21260, 27630, 35910, 46680, 60680, 78880 },
			{ 400, 520, 670, 870, 1130, 1460, 1890, 2450, 3180, 4130, 5360,
					6960, 9040, 11750, 15270, 19850, 25800, 33540, 43600, 56680 },
			{ 800, 1040, 1350, 1750, 2270, 2950, 3830, 4970, 6460, 8390, 10900,
					14170, 18420, 23940, 31120, 40450, 52580, 68350, 88850, 115500 },
			{ 560, 720, 930, 1200, 1560, 2020, 2620, 3400, 4420, 5740, 7460,
					9690, 12590, 16360, 21260, 27630, 35910, 46680, 60680, 78880 },
			{ 400, 520, 670, 870, 1130, 1460, 1890, 2450, 3180, 4130, 5360,
					6960, 9040, 11750, 15270, 19850, 25800, 33540, 43600, 56680 }
	};
	
	public int offenceMagic = 0;
	public int defenceMagic = 3;
	
	
	// �Ҹ��� ���� ���(����)
	final public int[] buyOffenseFire = {
			800,1040,1350,1750,2270,
			2950,3830,4970,6460,8390,
			10900,14170,18420,23940,31120,
			40450,52580,68350,88850,115500	};
	
	// �ٶ����� ���� ���(����)
	final public int[] buyOffenseWind = {
			560,720,930,1200,1560,
			2020,2620,3400,4420,5740,
			7460,9690,12590,16360,21260,
			27630,35910,46680,60680,78880};
	
	// �������� ���� ���(����)
	final public int[] buyOffenseCloud = {
			400,520,670,870,1130,
			1460,1890,2450,3180,4130,
			5360,6960,9040,11750,15270,
			19850,25800,33540,43600,56680};
	

	// �Ҹ��� ���� ���(���)
	final public int[] buyDefenseFire = {
			800,1040,1350,1750,2270,
			2950,3830,4970,6460,8390,
			10900,14170,18420,23940,31120,
			40450,52580,68350,88850,115500	};
	
	// �ٶ����� ���� ���(���)
	final public int[] buyDefenseWind = {
			560,720,930,1200,1560,
			2020,2620,3400,4420,5740,
			7460,9690,12590,16360,21260,
			27630,35910,46680,60680,78880};
	
	// �������� ���� ���(���)
	final public int[] buyDefenseCloud = {
			400,520,670,870,1130,
			1460,1890,2450,3180,4130,
			5360,6960,9040,11750,15270,
			19850,25800,33540,43600,56680};

	// �̱۸�� �⺻���� (��,��,��) {ȣ�ڼ�, �����, ���ӽð�, ���¼�}
	final public int[][] singleDefalutData = {
			{30, 3, 15, 0},
			{60, 3, 15, 0},
			{90, 3, 15, 0},
	} ;
	
	// ������� �⺻���� (��,��,��) {ȣ�ڼ�, �����, ���ӽð�, ���¼�}
	final public int[][] versusDefalutData = {
			{30, 3, 15, 3},
			{60, 3, 15, 3},
			{90, 3, 15, 3},
	} ;
	
	// ���ɼ� Ȯ��(������ȹ��??? ����/������??)
	int offenseProbability = 70;
	int duringTheOffenseFire = 20; 
	int duringTheOffenseWind = 30; 
	int duringTheOffenseCloud = 50; 
	
	int defenseProbability = 30;
	int duringTheDefenseFire = 20; 
	int duringTheDefenseWind = 30; 
	int duringTheDefenseCloud = 50; 
	
	//
	// ** ���� �˾�
	// * ��������
	// ã�� ȣ�ڼ�
	int searchPumpkin = 90; // ����
	// �����
	int watersOfLife = 3; // ����
	// �ҿ�ð�
	int LeadTime = 1000; // ����
	
	// ����Ʈ
	//�̱۸�� : (((ã�� ���ڼ� * ���� �����*1000) / �ҿ�ð�) * 2)			
	//������� : ((ã�� ���ڼ� * ���� �����*1000) / �ҿ�ð�) * 10
	// �̱�
	int point = searchPumpkin * watersOfLife * 1000 / LeadTime * 2; 
	// ����
	//int point = searchPumpkin * watersOfLife * 1000 / LeadTime * 10;
	
	// ����ġ (ȹ�� ����Ʈ * 1.5)
	int EXP = (int)(point * 1.5f);
	// ��� (ȹ�� ����Ʈ / 8)
	int gainGold = (int)(point /8);

	// ** ����Ʈ ���� �Ǵ� ����Ʈ ��� ���߿� �ϳ��� ���� **
	int winnersPoint = 200;
	
	// ����Ʈ ���� (�й�� �¸��� ����Ʈ�� / 3)
	int pointSubtraction = (int)(winnersPoint / 3);
	
	// ����Ʈ ��� ��� (����Ʈ ���� / 10)
	int pointDefense = pointSubtraction / 10;
	
	// ������ �� ���ź�/1ȸ ȹ����	4426 ????
	
	
	// ������ ����ġ �� ������ ����(���)
	public final int[] expPerLevel = {
			7000,			7350,			7710,			8090,			8490,
			8910,			9350,			9810,			10300,		10810,
			11350,		11910,		12500,		13120,		13770,
			14450,		15170,		15920,		16710,		17540,
			18410,		19330,		20290,		21300,		22360,
			23470,		24640,		25870,		27160,		28510,
			29930,		31420,		32990,		34630,		36360,
			38170,		40070,		42070,		44170,		46370,
			48680,		51110,		53660,		56340,		59150,
			62100,		65200,		68460,		71880,		75470
	};
	
	final int[] compensationPerLevel = {
			1000,			1050,			1100,			1150,			1200,
			1260,			1320,			1380,			1440,			1510,
			1580,			1650,			1730,			1810,			1900,
			1990,			2080,			2180,			2280,			2390,
			2500,			2620,			2750,			2880,			3020,
			3170,			3320,			3480,			3650,			3830,
			4020,			4220,			4430,			4650,			4880,
			5120,			5370,			5630,			5910,			6200,
			6510,			6830,			7170,			7520,			7890,
			8280,			8690,			9120,			9570,			10040
	};
	
	
	private String kPath = "UserData.plist";

	private Context mContext;
	private HashMap<String, Object> data;
	
	private static UserData userData;

	// #pragma mark - Singleton Methods
	public static synchronized UserData share(Context context) {
		if (userData == null) {
			userData = new UserData(CCDirector.sharedDirector().getActivity().getApplicationContext());
			Log.e("** UserData **", "make Single Instance");
		}
		return userData;
	}

	// ������ ����� �����͸� ������ ����, ������ ������ ����(File����x)
	private UserData(Context context) {
		mContext = context;

		
		// ���� ���� üũ(������ �����ϸ� �ҷ�����)
		try {
			
			// ������ �̹� ������ �ҷ�����,
			InputStream fis = mContext.openFileInput(kPath);
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			//StringBuffer sb = new StringBuffer();
			this.data = new HashMap<String, Object>();
			while (true) {
				String readLine = br.readLine();
				if (readLine == null)
					break;
					if ( !readLine.equals("")) {
						String[] key = readLine.split("=");
						/*
						Log.e("key", key[1]);
						String[] typeValue = key[1].split(")");
						mapType = mapType.substring(1);
						Log.e("mapType", mapType);
						*/
						String mapType = key[1].substring(1, key[1].indexOf(")"));
//						Log.e("mapType", mapType);
						String typeValue = key[1].substring(key[1].indexOf(")")+1);
//						Log.e("typeValue", typeValue);
						
						int i = key[1].indexOf(")"); // ���۰��� -1
						
						if (mapType.equals("Integer")) {
							int value = Integer.parseInt(typeValue);
							data.put(key[0], value);
							
							//long�� ��ȯ
						} else if (mapType.equals("Long")) {
							long value = Long.parseLong(typeValue);
							data.put(key[0], value);
							
						} else if (mapType.equals("Boolean")) {
							boolean value ;
							if (typeValue.equals("true")) {
								 value = true;
							} else {
								 value = false;
							}
							data.put(key[0], value);
	
						} else if (mapType.equals("String")) {
							String value = typeValue;
							data.put(key[0], value);
	
						} else if (mapType.equals("ArrayList")) {
							String temp = typeValue.substring(1, typeValue.length()-1);
							String[] ArrayData = temp.split(",");
							ArrayList<Integer> value = new ArrayList<Integer>();
							
							for (int j = 0; j < ArrayData.length; j++) {
								if (!ArrayData[j].equals("") && ArrayData[j] != null) {
									 value.add( Integer.parseInt(ArrayData[j].replace(" ", "")));									
								}
							}
							data.put(key[0], value);
						}
					}
				//sb.append(readLine + "\n");
			}
			br.close();
			fis.close();
			//String hashmapData = sb.toString();

			//Toast.makeText(mContext, "���Ͽ��� ������ �ҷ����� ����", Toast.LENGTH_SHORT).show();
			
		} catch (FileNotFoundException e) {
			
			//Toast.makeText(mContext, "���� ������ �����", Toast.LENGTH_SHORT).show();
			// e.printStackTrace();
//			Log.e("Exception", "FileNotFoundException");
			// ������, �ʱⰪ���� �����Ѵ�.
			this.data = new HashMap<String, Object>();
			data.put("LevelCharacter", 1); // ���� �ӽ� ����. Default���� 0��
			data.put("LevelFire", 1);
			data.put("LevelWind", 1);
			data.put("LevelCloud", 1);
			data.put("LevelDivine", 0);
			data.put("LevelEarth", 0);
			data.put("LevelMirror", 0);
			data.put("SphereNumber", 3); // �⺻3��(�߰� ���� ���۰��� 0����) + ���Ű���
			data.put("Exp", 7777L); // ���� �ӽ� ����. Default���� 0��
			data.put("Gold", 0L);
			data.put("Point", 654897L); // ���� �ӽ� ����. Default���� 0��
			data.put("HistoryWin", 77); // ���� �ӽ� ����. Default���� 0��
			data.put("HistoryLose", 44); // ���� �ӽ� ����. Default���� 0��
			data.put("ReceivedBroomstick", 0); // Default���� 6��
			data.put("broomstickRenewalTimeData", 1999999999999L); // ���ڷ� �Ҹ�� ����(�ð�)
			
			// ���������δ� 1�� 1ȸ�� �����ϰ� �ؾߵ�.
			// ���� DB�� �־�ߵ�.
			data.put("daily", 1); // �⼮ �ϼ�

			ArrayList<Integer> emoticonArray = new ArrayList<Integer>();
			emoticonArray.add(101);
			emoticonArray.add(102);
			emoticonArray.add(103);
			data.put("Emoticon", emoticonArray);

			//
			// ��Ÿ��

			// ģ������� �ҷ��´�
			// ģ�����̵� 1)�ʴ�, 2)���ڷ� ��Ÿ��(�ֱ� �߼� �ð�)
			data.put("Pester", true);
			data.put("SendBroomstick", true);

			//
			// ����
			data.put("OptionBGM", true);
			data.put("OptionSFX", true);
			data.put("OptionRefuseInvitation", false);
			data.put("OptionReceiveBloomstick", true);
			data.put("OptionShowMyPicture", true);
			data.put("OptionNoviceGuide", false);
			

			

			updateUserData(data);
	
		} catch (UnsupportedEncodingException e) {
			Log.e("Exception", "UnsupportedEncodingException");
		} catch (IOException e) {
			Log.e("Exception", "IOException");
		}
	}

	//
	// kPath ���ϸ����� ��������
	private void updateUserData(final HashMap<String, Object> userData) {

		final Set<Entry<String, Object>> set = userData.entrySet();
		if (set.equals("")) {
			Log.e("updateUserData", "UserData ������ �����ϴ�.");
		}

		CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
			public void run() {
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
				try {
					String xml = "";
					Set mapKey = userData.keySet();
					String[] splitLine = mapKey.toString().substring(1,mapKey.toString().length()-1).split(", ");
					//Log.e("mapKey", mapKey.toString().substring(1,mapKey.toString().length()-1));
					for (int i = 0; i < userData.size(); i++) {

						// Log.e("containsKey(splitLine[i])"+i,splitLine[i]+":"+userData.containsKey(splitLine[i]));
						if (userData.containsKey(splitLine[i])) {
							String type = "";
							if (userData.get(splitLine[i].toString()) instanceof String) {
								type = "String";
							} else if (userData.get(splitLine[i].toString()) instanceof ArrayList) {
								type = "ArrayList";
							} else if (userData.get(splitLine[i].toString()) instanceof Integer) {
								type = "Integer";
							} else if (userData.get(splitLine[i].toString()) instanceof Long) {
								type = "Long";
							} else if (userData.get(splitLine[i].toString()) instanceof Boolean) {
								type = "Boolean";
							}
							xml += splitLine[i] + "=(" + type + ")"
									+ userData.get(splitLine[i]).toString()
									+ "\r\n";
							// xml += "\r\n" + splitLine[i] + "="	+ userData.get(splitLine[i]).toString();
						}
					}
					xml += "\r\n";
					xml = xml.replace("\r\n\r\n","");
					//xml = xml.replace (" ","");
					//Toast.makeText(mContext, "** xml **\r\n"+xml, Toast.LENGTH_LONG).show();
					
					// in memory
					FileOutputStream fos = mContext.openFileOutput(kPath,Context.MODE_PRIVATE);
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					//osw.write(set.toString());
					osw.write(xml);
					osw.flush();
					osw.close();
					fos.close();
					//Toast.makeText(mContext, "write success!!",Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					//Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		//Toast.makeText(mContext, "UserData ���Ϸ� ���� �Ϸ� \r\n" + set.toString(),Toast.LENGTH_LONG).show();
	}
	// updateUserData() end

	// #pragma mark - data methods general
	// �̸�Ƽ�� ������ �ѹ��� ����ȯ �ؾߵ�. ArrayList --> int��
	private int getByKey(String key) {
		return (Integer) this.data.get(key);
	}

	private void setByKey(String key, int value) {
		this.data.put(key, value);
		this.updateUserData(this.data);
	}
	
	private long getLongByKey(String key) {
		return (Long)this.data.get(key);
	}

	private void setLongByKey(String key, long value) {
		this.data.put(key, value);
		this.updateUserData(this.data);
	}

	private boolean getBoolByKey(String key) {
		return (Boolean) this.data.get(key);
	}

	private void setBoolByKey(String key, boolean value) {
		//Log.e(key + "-before", "" + data.put(key, (Boolean)value));
		this.data.put(key, (Boolean)value);
		//Log.e(key+ "-after", "" + data.put(key, (Boolean)value));
		this.updateUserData(this.data);
	}
	
	// #pragma mark - data methods
	public int getLevelByCategory(String keyString) {
		return (Integer) this.getByKey(keyString);
	}

	private void setLevelByCategory(String keyString, int level) {
		this.setByKey(keyString, level);
	}

	public void levelUpByCategory(String keyString) {
		int level = this.getLevelByCategory(keyString) + 1;
		this.setLevelByCategory(keyString, level);
	}

	public int getSphereNumber() {
		return this.getByKey("SphereNumber");
	}

	public void setSphereNumber(int number) {
		this.setByKey("SphereNumber", number);
	}

	public void increaseSphere() {
		int sphereNumber = this.getSphereNumber() + 1;
		this.setSphereNumber(sphereNumber);
	}

	public long getExp() {
		return (Long) this.getLongByKey("Exp");
	}

	public void setExp(long number) {
		this.setLongByKey("Exp", number);
	}

	long nextLevelExp = 5000; // temporary

	public void addExp(long number) {
		// ���� : ����������ġ�� ���� ������
		long exp = this.getExp() + number;
		exp %= nextLevelExp;
		this.setExp(exp);
	}

	public long getGold() {
		return this.getLongByKey("Gold");
	}

	public void setGold(long gold) {
		this.setLongByKey("Gold", gold);
	}

	public void addGold(long deltaGold) {
		long gold = this.getGold() + deltaGold;
		this.setGold(gold);
	}

	//
	// false : ������
	// true : ����ó��
	public boolean reduceGold(long deltaGold) {
		long gold = this.getGold() - deltaGold;
		if (gold >= 0) {
			this.setGold(gold);
			return true;
		} else {
			return false;
		}
	}

	public long getPoint() {
		return this.getLongByKey("Point");
	}

	public void setPoint(long point) {
		this.setLongByKey("Point", point);
	}

	public void addPoint(long deltaPoint) {
		long point = this.getPoint() + deltaPoint;
		this.setPoint(point);
	}

	public void reducePoint(long deltaPoint) {
		long point = this.getPoint() - deltaPoint;
		point = (long) CGPoint.clampf(point, 0, point);
		this.setPoint(point);
	}

	//
	// return -1 : ����
	// ���ǰ� : ����ó��
	public int getHistory(int result) {
		if (result < 0 || result > 1)
			return -1;

		String keyString = result == kResultWin ? "HistoryWin" : "HistoryLose";
		return this.getByKey(keyString);
	}

	public int increaseHistory(int result) {
		if (result < 0 || result > 1)
			return -1;

		String keyString = result == kResultWin ? "HistoryWin" : "HistoryLose";
		this.setByKey(keyString, 1);
		return 0;
	}

	public int getBroomstick() {
		return this.getByKey("ReceivedBroomstick");
	}

	public void setBroomstick(int number) {
		if (number <= homeMaxBroomstick && number >= 0) {
			this.setByKey("ReceivedBroomstick", number);
		} else {
			CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
						public void run() {
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
							// Toast.makeText(mContext, "���ڷ簡 ���� á���ϴ�.",
							// Toast.LENGTH_SHORT).show();
						}
					});
		}
	}

	public void addBroomstick(int deltaNumber) {
		int broomstick = this.getBroomstick() + deltaNumber;
			this.setBroomstick(broomstick);
	}
	
	public void myBroomstick() {
		int broomstick = this.getBroomstick() -1;
		this.setBroomstick(broomstick);
	}
	
	public ArrayList<Integer> getEmoticons() {
		return (ArrayList<Integer>) this.data.get("Emoticon");
	}

	public void addEmoticon(int emoticonId) {
		ArrayList<Integer> emoticons = getEmoticons();
		emoticons.add(emoticonId);
		this.data.put("Emoticon", emoticons);
		this.updateUserData(data);
	}
	
	//
	// ģ������ ������
	public boolean getPester() {
		return this.getBoolByKey("Pester");
	}
	
	public void setPester() {
		boolean state = this.getBoolByKey("Pester");
		this.setBoolByKey("Pester", !state);
	}
	
	//
	// ģ������ ������ ������
	public boolean getSendBroomstick() {
		return this.getBoolByKey("SendBroomstick");
	}

	public void setSendBroomstick() {
		boolean state = this.getBoolByKey("SendBroomstick");
		this.setBoolByKey("SendBroomstick", !state);
	}
	
	//
	// Options
	public boolean getOption(int type) {
		String keyString = "OptionBGM,OptionSFX,OptionRefuseInvitation,OptionReceiveBloomstick,OptionShowMyPicture,OptionNoviceGuide";
		String[] keyArray = keyString.split(",");
		return this.getBoolByKey(keyArray[type]);
	}

	//
	// �ɼǰ��� ��۽�Ų��. YES -> NO, NO -> YES
	public void toggleOption(int type) {
		String keyString = "OptionBGM,OptionSFX,OptionRefuseInvitation,OptionReceiveBloomstick,OptionShowMyPicture,OptionNoviceGuide";
		String[] keyArray = keyString.split(",");
		boolean state = this.getOption(type);
		this.setBoolByKey(keyArray[type], !state);
	}

	public int getTimeMaxBroomstick() {
		return timeMaxBroomstick;
	}
	
	public long getBroomstickRenewalTimeData() {
		return this.getLongByKey("broomstickRenewalTimeData");
	}
	
	public void setBroomstickRenewalTimeData(long value) {
		this.setLongByKey("broomstickRenewalTimeData", value);
	}
	
	public void addBroomstickRenewalTimeData() {
		this.setBroomstickRenewalTimeData(this.getBroomstickRenewalTimeData() + this.broomstickRenewalTime);
		this.addBroomstick(1);
	}
	
	public int getDailyCount() {
		return this.getByKey("daily");
	}

	public void setDailyCount(int dailyCount) {
		this.setByKey("daily", dailyCount);
	}
	
	
	
}
// end



// Ǯ�н� ������ġ : ��Ű��/files/���ϸ�
// ���ý� ������ġ : data/data/��Ű��/files/���ϸ�

//MODE_PRIVATE  ȥ�ڸ� ����ϴ� ��Ÿ���� ���� ���� ���� .. (����Ʈ) 
//MODE_APPEND  ������ �̹� ������ ��� ����� ���� ���� �ʰ� �߰� ���� ����. ( ���� ���뿡 �߰� ) 
//MODE_WORLD_READABLE  �ٸ� ���� ���α׷��� ���� �� �ֵ��� ��� 
//MODE_WORLD_WRITEABLE  �ٸ� ���� ���α׷��� �� �� �ֵ��� ��� 

//Set<Entry<String, Object>> set = hashMap.entrySet();
//while(set.iterator().hasNext()){
//	//����..
//}

/*
private void kkk() {

	try{
		//in memory
		FileOutputStream fos=openFileOutput("inmemory_out.txt", MODE_PRIVATE);
		OutputStreamWriter osw=new OutputStreamWriter(fos);
		osw.write(writeStr);
		osw.flush();
		Toast.makeText(getApplicationContext(),
				"write success!!", 
				Toast.LENGTH_SHORT)
				.show();
		writeET.setText("");
	}catch (Exception e) {
		
		Toast.makeText(getApplicationContext(),
				e.getMessage(), 
				Toast.LENGTH_SHORT)
				.show();
	}
}
	*/
/*
private void ooo() {

String FILENAME = "hello_file";
String string = "hello world!";

try {
    FileOutputStream fos = getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
    fos.write(string.getBytes());
    fos.flush();
    fos.close();
} catch (IOException e) {
    Log.e("STACKOVERFLOW", e.getMessage(), e);
}

try {
    FileInputStream fis = getApplicationContext().openFileInput("hello_file");
    byte[] buffer = new byte[(int) fis.getChannel().size()];
    fis.read(buffer);
    String str= "";
    for(byte b:buffer) str+=(char)b;
    fis.close();
    Log.i("STACKOVERFLOW", String.format("GOT: [%s]", str));
} catch (IOException e) {
    Log.e("STACKOVERFLOW", e.getMessage(), e);
}
}
*/

/*
private void aaa() {

	String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	File dir = new File(sdPath, "testing"); // sd��ο� testing�̶�� ������ ���� �غ�
	dir.mkdir(); // testing ���� ����
	File file = new File(dir, "testFile.txt"); // file.txt �����

	FileOutputStream fos;
	try {
		fos = new FileOutputStream(file);
		// file.txt�� �Է� ��Ʈ��(����) �ű�
		// String str = mEdit.getText().toString(); //editText�� �̿��Ͽ� ���� �Է�
		String str = "�Է��� �� �Ǿ��°�? ��������"; // �ӽ�
		fos.write(str.getBytes());
		// ��Ʈ�� �о� �ֱ�
		fos.close(); // ��Ʈ������(���� �̱�)
		// mEdit.setText("write success"); //editText�� �Ϸ� ���� ���
		Log.e("FileOutputStream", "write success");
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
*/
/*
private void bbb() {;
 FileInputStream in;
	try {
		in = getApplicationContext().openFileInput("filename.txt");
		InputStreamReader inputStreamReader = new InputStreamReader(in);
		BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line);
		}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
*/
/*
private void ccc() {
FileOutputStream fos = getApplicationContext().openFileOutput("test.txt", Context.MODE_PRIVATE);
String str = "Android File IO Test";
fos.write(str.getBytes());
fos.close();
FileInputStream fis = getApplicationContext().openFileInput("test.txt");
byte[] data = new byte[fis.available());
while (fis.read(data) != -1){;}
fis.close();
 
InputStream fres = getResources().openRawResource(R.raw.restext);
byte[] data = new byte[fres.available());
while(fres.read(data) != -1) { ; }
fres.close()
 deleteFile("test.txt");
}
*/

/*
public static boolean sync(FileOutputStream stream) {
	 try {
		 if (stream != null) {
			 stream.getFD().sync();
		 }
		 return true;
	 } 
	 catch (IOException e) { }
	 return false;
}
*/
