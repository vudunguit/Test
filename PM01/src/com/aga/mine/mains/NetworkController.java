package com.aga.mine.mains;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.aga.mine.pages.Game;
import com.aga.mine.pages.GameMinimap;
import com.aga.mine.pages.UserData;
import com.aga.mine.util.Util;

//import com.aga.mine.layers.GameInvite;
//import com.aga.mine.layers.GameRandom;
//import com.aga.mine.layers.Home;
//import com.aga.mine.layers.InvitationReceiver;
//import com.aga.mine.pages.Game;
//import com.aga.mine.pages.GameMinimap;

public class NetworkController extends Activity {
	
	// 네트워크 상황
	final static int kNetworkStateNotAvailable = 0;
	final static int kNetworkStateTryingStreamOpen = 1;
	final static int kNetworkStateStreamOpend = 2;
	final static int kNetworkStateReconnectiogForError = 3;
	final static int kNetworkStateConnectionCompleted = 4;
	final static int kNetworkStateDisconnected = 5;
	final static int kNetworkStateTryingReconnect = 6;
	final static int kNetworkStatePendingMatchResult = 7;
	final static int kNetworkStateMatchCompleted = 8;
	final static int kNetworkStateMatchFailed = 9;
	/*******************************************/
	// 매치서버와 통신하는 메시지 (S :서버가 보내는 메시지, C : 클라이언트가 보내는 메시지 A : 모두 쓰는 메시지) 
	final static int kMessageConnectionCompletedServerConfirm = 0; // S_접속 성공
	final static int kMessagePlayerInformation = 1; // C_플레이어 정보
	final static int kMessageRequestMatch = 2; // C_랜덤 매치 요청
	final static int kMessageMatchCompleted = 3; // S_매치 성공
	final static int kMessageMatchFailed = 4; // S_매치 실패
	final static int kMessagePlayData = 5; // A_게임 데이터
	final static int kMessageGameOver = 6; // A_게임 오버시 점수와 함께 보낸다
	final static int kMessageDisconnected = 7; // 연결 끊김??? (사용안함)
	final static int kMessageInManagement = 8; // 중복 메시지, (사용안함) 그러나 삭제해선 안됨, 131128
	final static int kMessageOpponentConnectionLost = 9; // S_상대방 연결 실패
	final static int kMessageRequestIsPlayerConnected = 10; // A_유저의 접속 체크
	final static int kMessageRequestMatchInvite = 11; // C_초대 매치 요청
	final static int kMessageInSystemManagement = 12; // 점검중? (사용안함)
	final static int kMessageInRoomOwner = 13; // C_방장 권한 부여
	final static int kMessageRequestGameOver = 14; // A_게임 종료 (내 점수를 함께 보낸다)	
	final static int kMessageRequestScore = 15; // S_게임 종료
	final static int kMessageGameReady = 16; // C_게임 준비 완료메시지
	final static int kMessageGameStart = 17; // S_게임 시작 (서버가 보내는 메시지)
	final static int kMessageRequestInvite = 18; // 초대 매치용 요청 메시지 (사용안함)
	final static int kMessageResponseMatchInvite = 19; // 초대 대전 응답 (사용안함)
	final static int kMessageResponseInvite = 20; // 초대 매치용 응답 메시지 (사용안함)
	final static int kMessageWillYouAcceptInvite = 21; // S_초대 매치 요청 메시지
	final static int kMessageWillYouAcceptInviteOK = 22; // C_초대 매치 응답 메시지
	final static int kMessagePlayerIdle = 23; // (사용안함)
	/*******************************************/
	// 플레이 데이타 (플레이 중 발생되는 매치 데이타) 분류 코드
	final static int kPlayDataCellOpen = 0;
	final static int kPlayDataMushroomOn = 1;
	final static int kPlayDataMushroomOff = 2;
	final static int kPlayDataMagicAttack = 3;
	final static int kPlayDataMagicDefense = 4; // 공격 방어 구분 이유??
	final static int kPlayDataGameOver = 5;
	final static int kPlayDataEmoticon = 6;
	final static int kPlayDataMine = 7;
	final static int kPlayDataSphere = 8;
	final static int kPlayDataSphereTake = 9;
	/*******************************************/
	// 마법공격()에 대한 세부 마법분류
	final static int kSphereTypeFire  = 1;
	final static int kSphereTypeWind = 2;
	final static int kSphereTypeCloud  = 3;
	final static int kSphereTypeDivine  = 4;
	final static int kSphereTypeEarth  = 5;
	final static int kSphereTypeMirror  = 6;
	/*******************************************/
	// 게임 난이도 세부 마법분류
	final static int kIsRoomOwnerEasy = 1;
	final static int kIsRoomOwnerNormal = 2;
	final static int kIsRoomOwnerHard = 3;
	final static int kIsRoomEasy = 1;
	final static int kIsRoomNormal = 2;
	final static int kIsRoomHard = 3;
	/*******************************************/
	// 보내는 모드 / 받는 모드
	final static int kModeSent = 0;
	final static int kModeReceived = 1;
	/*******************************************/	
	static NetworkControllerDelegate delegate;

	public static String kTempFacebookId = "999999999999999";
	public static String kTempName = "@Guest";

	private enum NetworkState{}
	private enum MessageType{}

	final static int kTempLevel = 15;

//	final static String kServerHost = "192.168.10.232"; // 회사 내부 테스트용
	final static String kServerHost = "211.110.139.226"; // cafe24 상용서버
	final static int kServerPort = 8007;

	public OutputStream outStream_;
	public InputStream inStream_;
	public ByteBuffer inBuffer_; // , outBuffer_;

	private Socket socket;
	
	private static NetworkController androidClient;
	Context mContext;
	UserData userData;
	
	private ExecutorService mExecutorService;
	
	public static synchronized NetworkController getInstance() {
		if (androidClient == null) {
			androidClient = new NetworkController();
			Log.e("** NetworkController **", "make Single Instance");
		}
		return androidClient;
	}
	//
	// 초기화
	private NetworkController() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
//		if (FacebookData.getinstance().getUserInfo() != null) {
			kTempFacebookId  = FacebookData.getinstance().getUserInfo().getId();
			kTempName  = FacebookData.getinstance().getUserInfo().getName();
//		if (userData.facebookUserInfo != null) {
//			kTempFacebookId  = userData.facebookUserInfo.getId();
//			kTempName = userData.facebookUserInfo.getName();			
//		}
		
		
//		Log.e("NetworkController", "UserData.share(this).facebookUserInfo.getId() : " + UserData.share(this).facebookUserInfo.getId());
//		Log.e("NetworkController", "UserData.share(this).facebookUserInfo.getUsername() : " + UserData.share(this).facebookUserInfo.getUsername());
		// this.setState(state);
		// connect(kServerHost, kServerPort);
		
		//네트웍 write 쓰레드를 순서대로 실행하기 위해서 1개의 큐를 가지는 쓰레드 서비스 생성
		mExecutorService = Executors.newFixedThreadPool(1);
		
		//Read Thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				connect(kServerHost, kServerPort);
			}
		}).start();
	}
	
	//	private void setDetails(String string) {
	//		if (delegate != null) {
	//			delegate.setDetails(string);
	//		}
	//	}
	
	// 도우미 sharedController
	public Socket getSocket() throws IOException {
	
		if (socket == null)
			socket = new Socket();
	
		if (!socket.isConnected())
			socket.connect(new InetSocketAddress(kServerHost, kServerPort));
		return socket;
	}

	public void closeSocket() throws IOException {
		if (socket != null)
			socket.close();
	}

	private void setState(NetworkState state) {
//		state_ = state;
//		if (delegate != null) {
//			delegate.stateChanged(state);
//		}
	}
	
//	private void setMessage(MessageType state, int mode) {
//		if (delegate != null) {
//			if (mode == kModeSent) {
//				delegate.messageSent(state);
//			} else if (mode == kModeReceived) {
//				 delegate.messageReceived(state);
//			}
//		}	
//	}
	
	private void setMessage(int state, int mode) {
		if (delegate != null) {
			if (mode == kModeSent) {
				delegate.messageSent(state);
			} else if (mode == kModeReceived) {
				 delegate.messageReceived(state);
			}
		}	
	}
	
// 수신된 메시지 처리
	private void checkForMessages() throws IOException {
		Log.e("NetworkController / checkForMessages", "in");
		inBuffer_.order(ByteOrder.nativeOrder());
		// lim = post, pos = 0
		inBuffer_.flip();
		while (true) {
//			Log.e("NetworkController", "buffer is : " + inBuffer_.toString());
			byte messageLength = inBuffer_.get();
			Log.e("NetworkController", "message length is : " + messageLength);
			if (messageLength == 0)
				continue;
	
			// read message
			Log.e("NetworkController", "messageLength : " + messageLength);
			Log.e("NetworkController", "inBuffer_.remaining() : " + inBuffer_.remaining());
			//byte[] read = new byte[messageLength];
			final byte[] read = new byte[inBuffer_.remaining()];
 			inBuffer_.get(read);
 			
			processMessage(read);
	
			inBuffer_.compact();
			break;
	

		}
	}
	
	private void processMessage(byte[] data) throws IOException {
		MessageReader reader = new MessageReader(data);
		final byte messageType = reader.readByte();		

		// 디버그 코드
		final String[] messageTypeString = {
				"kMessageConnection",
				"kMessageConnectionCompletedServerConfirm",
				"kMessagePlayerInformation",
				"kMessageRequestMatch",
				"kMessageMatchCompleted",
				"kMessageMatchFailed",
				"kMessagePlayData",
				"kMessageGameOver",
				"kMessageDisconnected",
				"kMessageInManagement",
				"kMessageOpponentConnectionLost",
				"kMessageRequestIsPlayerConnected",
				"kMessageRequestMatchInvite",
				"kMessageInSystemManagement",
				"kMessageInRoomOwner",
				"kMessageRequestGameOver",
				"kMessageRequestScore",
				"kMessageGameReady",
				"kMessageGameStart"
				};
		
		final String[] dataTypeString = {
			"kPlayDataCellOpen",
			"kPlayDataMushroomOn",
			"kPlayDataMushroomOff",
			"kPlayDataMagicAttack",
			"kPlayDataMagicDefense",
			"kPlayDataGameOver",
			"kPlayDataEmoticon",
			"kPlayDataMine",
			"kPlayDataSphere",
			"kPlayDataSphereTake"};
		
		String matchedOppenentFacebookId = " ";
		String matchedOppenentName = " ";
		int result;
		char dataType;
		int dataValue = 0;
		Log.e("NetworkController", "messageType : " + messageType);
		
		switch (messageType) {
		case kMessageConnectionCompletedServerConfirm:
			Log.e("NetworkController", "ConnectionCompletedServerConfirm received");
			//setState(kNetworkStateConnectionCompleted);
			sendPlayerInformation();
			//sendRequestMatch();
			break;
			
		case kMessageInSystemManagement:
			Log.e("NetworkController", "kMessageworkStateDisconnected");
			//setstate(kMessageworkStateDisconnected);
		break;
			
		case kMessageMatchFailed:
			Log.e("NetworkController", "kMessageMatchFailed");
//			NetworkController.getInstance().sendRoomOwner(1); // 방장 권한
			sendRoomOwner(1); // 방장 권한
			break;
			
			
		case kMessagePlayData:
			Log.e("NetworkController", "kMessagePlayData");
			int count = 0;
			while (reader.buffer_.hasRemaining()) {
				GameMinimap.getInstance().receivePlayData(reader.readByte(), reader.readInt());
				// kMessageRequestIsPlayerConnected와 같이 수정해도 될듯
				if (reader.buffer_.hasRemaining()) {
					count++;
					reader.buffer_.position(1 + 10 * count);
				}
			}

//			ByteInt[] playData = new ByteInt[(data.length /10) +1];
//
//			byte messageTypeGarbage;
//			int dataSizeGarbage;
//			int count = 0;
//			Log.e("GameMinimap", "receivePlayData reader.length() : " + reader.length());
//			for (int i = 0; i < (data.length /10) +1 ; i++) {
//				GameMinimap.getInstance().receivePlayData(reader.readByte(), reader.readInt());
////				playData[i].setPlayType(reader.readByte());		
////				playData[i].setData(reader.readInt());
//				if (count != data.length /10) {
//					dataSizeGarbage = reader.readInt();
//					messageTypeGarbage = reader.readByte();
//					count ++;
//				}
//			}
			
//			GameMinimap.getInstance().receivePlayData(playData);
//			GameMinimap.getInstance().receivePlayData(data);
//			GameMinimap.getInstance().receivePlayData(reader.readByte(), reader.readInt());
			break;

		case kMessageOpponentConnectionLost:
			Log.e("NetworkController", "kMessageOpponentConnectionLost");
			break;			
			
		case kMessageRequestIsPlayerConnected:
//			Log.e("NetworkController", "kMessageRequestIsPlayerConnected");
//			byte[] tempData = data;
//			Log.e("Network", "data 크기 : " + tempData.length);
//			for (byte b : tempData) {
//				Log.e("Network", "내용 [" + b + "]");
//			}
			while (reader.buffer_.hasRemaining()) {
//				Log.e("NetworkController", "position before : " + reader.buffer_.position());
				String id = reader.readString();
				byte joinValue = reader.readByte();
				Util.setJoin(id, joinValue);
//				Log.e("NetworkController", "id.lenght : " + id.length());
				if (reader.buffer_.hasRemaining()) {
//					Log.e("NetworkController", "position after : " + reader.buffer_.position());
					reader.readInt(); // dataSize
					reader.readByte(); // connenct_dataType // or
//					reader.buffer_.position(reader.buffer_.position() + 5); // or
//					reader.buffer_.position(1 + (dataSize + 5) * count1); 
//					Log.e("NetworkController", "dataSize : " + dataSize + ", connenct_dataType : " + connenct_dataType );
				}
			}
			break;

//			
//		case kMessageGameOver:
//			Log.e("NetworkController", "kMessageGameOver");
//			Game.HudLayer.gameOver();
////			getInstance().sendPlayDataGameOver(98765);
////			NetworkController.getInstance().sendPlayDataGameOver(98765);
//			break;
//			
//		case kMessageRequestGameOver:
//			Log.e("NetworkController", "kMessageRequestGameOver");
//			Game.HudLayer.gameOver();
////			NetworkController.getInstance().sendPlayDataGameOver(98765);
//			break;
//			
		case kMessageRequestScore:
			Log.e("NetworkController", "kMessageRequestGameOver");
//			byte result1 = reader.readByte();
//			Log.e("Network", "상대방 점수 : " + result1);
			
			Log.e("Network", "data 크기 : " + data.length);
			int result2 = reader.readInt();
			Log.e("Network", "상대방 점수 : " + result2);
//			Game.HudLayer.gameOver();
			Game.HudLayer.gameOver(1, 1);
			
//			NetworkController.getInstance().sendPlayDataGameOver(98765);
			break;
			
		case kMessageGameStart:
			Log.e("NetworkController", "kMessageGameStart - game start!");
			Game.bbbbb();
			break;
			
		case kMessageMatchCompleted:
			Log.e("NetworkController", "kNetworkStateMatchCompleted");
			matchedOppenentFacebookId = reader.readString();
			matchedOppenentName = reader.readString();
			Log.e("NetworkController - kMessageMatchCompleted", "ID & Name /" + matchedOppenentFacebookId + " : " + matchedOppenentName);
			Log.e("NetworkController - kMessageMatchCompleted", "owner : " + owner);
			
			// 받으면 상대방 사진 및 이름 표시하고 게임 시작 카운터 돌리기
			count(GameInvite.backboard);
			
//			if (owner) {
//				GameRandom.matchNameReceiver(kTempName, matchedOppenentFacebookId); // gamerandom 임시로 막음
//			} else {
//				GameRandom.getInstance().matchNameReceiver(matchedOppenentFacebookId, kTempName);	
//			}
			
			break;
			
		case kMessageRequestInvite:
			Log.e("NetworkController", "kMessageRequestInvite");
			result = reader.readByte();
			matchedOppenentFacebookId = reader.readString();
			Log.e("NetworkController", "난이도 & ID /" + result + " : " + matchedOppenentFacebookId);
			InvitationReceiver.getInstance().exposePopup();
			InvitationReceiver.getInstance().setUserName(result, matchedOppenentFacebookId);
			result = 0;
			break;
	
		case kMessageResponseInvite:
			Log.e("NetworkController", "kMessageResponseInvite");
			result = reader.readByte();
			matchedOppenentFacebookId = reader.readString();			
			Log.e("NetworkController", "수락여부 & ID /" + result + " : " + matchedOppenentFacebookId);
			if (result == 1) {
//				if (owner) {
//					GameInvite.getInstance().matchNameReceiver(kTempName, matchedOppenentFacebookId); // gameinvite 임시로 막음
//				} else {
//					GameInvite.getInstance().matchNameReceiver(matchedOppenentFacebookId, kTempName);	
//				}
			} else {
				Log.e("NetworkController", "kMessageResponseInvite 실패");
			}
			result = 0;
			break;
			
		case kMessageWillYouAcceptInvite:
			Log.e("NetworkController", "kMessageWillYouAcceptInvite");
			result = reader.readByte();
			matchedOppenentFacebookId = reader.readString();			
			Log.e("NetworkController", "초대 요청 난이도 & ID /" + result + " : " + matchedOppenentFacebookId);
			RequestMatch.inviteMatch(matchedOppenentFacebookId, result);
			break;
			
		default:
			Log.e("NetworkController", "정의되지 않은 메시지가 수신되었습니다. : " + messageType);
			break;
		}
		
	}
	
	// StartLayer.StartLayer()
	// 서버 접속
	public boolean isInternetConnectionOk() {
		Log.e("NetworkController", "isInternetConnectionOk");
		
		// internet reachability
		boolean exists = false;

		try {
//			SocketAddress sockaddr = new InetSocketAddress(kServerHost, kServerPort);
//			// Create an unbound socket
//			Socket sock = new Socket();
//
//			// This method will block no more than timeoutMs.
//			// If the timeout occurs, SocketTimeoutException is thrown.
//			int timeoutMs = 2000; // 2 seconds
//			sock.connect(sockaddr, timeoutMs);
			getSocket();
			exists = true;
			Log.e("NetworkController", "try end");
		} catch (Exception e) {
		} finally {
			Log.e("NetworkController", "finally : " + exists);
			return exists;
		}
	}

	private void connect(String ip, int port) {
		Log.e("AndroidClient / connect", "connect in");
		//
		// (1) Open a socket
	
		try {
			socket = new Socket(kServerHost, kServerPort);
	
			//
			// (2) Open an input and output stream to the socket
			inStream_ = socket.getInputStream();
			outStream_ = socket.getOutputStream();
	
			// 1024크기의 버퍼 생성
//			inBuffer_ = ByteBuffer.allocate(1024);
			inBuffer_ = ByteBuffer.allocateDirect(1024);
			// lim = cap, pos = 0 
			inBuffer_.clear();
	
			//
			// (3) Communication
			byte[] read = new byte[1024];
	
			while (true) {
				int len = inStream_.read(read); // 메소드가 받은데이타가 있어야 실행됨.
				if (len > 0) {
					byte[] data = new byte[len];
	
					for (int i = 0; i < len; ++i) {
						data[i] = read[i];
					}
	
					// add to inBuffer (내용 저장)
					inBuffer_.put(data);
					checkForMessages();
				}
			}
	
			//
			// (4) Close the streams
			// socket.close();
			
		} catch (ConnectException e) {
			System.out.println(e.getMessage() 
					+ "\nConnection refused = 서버가 닫혀있다.\n서버가 on 상태인지, 입력한 ip 또는 port number가 올바른지 확인하시오.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() throws IOException {
		Log.e("AndroidClient / disconnect", "disconnect in");
		socket.close();
		//CCDirector.sharedDirector().end();
		//CCDirector.sharedDirector().getActivity().finish();
	}
	
	private void reconnect() throws IOException {
		this.disconnect();
		connect(kServerHost, kServerPort);
	}
	
	private void stream() {
	}
	
	private void iutputStreamHandleEvent() {
	}
	
	private void outputStreamHandleEvent() {
	}
	
	private boolean writeChunk() {
		return false;
	}
	
	private void sendData(byte[] data) {
		int dataLength = data.length;

		final MessageWriter message = new MessageWriter();
		message.writeInt(dataLength);
		message.writeBytes(data);

		mExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					outStream_.write(message.data_);
					Log.e("NetworkController / sendData","called !");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/******************* 서버 보내기 메소드들 ********************/
	public void sendGameReady() throws IOException {
		Log.e("NetworkController", "sending GameReady ......");
		MessageWriter message = new MessageWriter();
		message.writeByte((byte) kMessageGameReady);
		sendData(message.data_);
		setMessage(kMessageGameReady, kModeSent);
		Log.e("NetworkController", "sending GameReady");
	}
	
	public void sendGameOver() throws IOException {
		Log.e("NetworkController", "sending GameOver ......");
		MessageWriter message = new MessageWriter();
		message.writeByte((byte) kMessageGameOver);
		sendData(message.data_);
		setMessage(kMessageGameOver, kModeSent);
		Log.e("NetworkController", "sendGameOver");
	}
	
	// Game.gameOver()
	public void sendRequestGameOver(int point) throws IOException {
		Log.e("NetworkController", "sending RequestGameOver ......");
		MessageWriter message = new MessageWriter();
		message.writeByte((byte) kMessageRequestGameOver);
		message.writeInt(point);
		sendData(message.data_);
		setMessage(kMessageRequestGameOver, kModeSent);
		Log.e("NetworkController", "sending RequestGameOver");
	}
	
	// 서버 접속시 서버에 보냄
	private void sendPlayerInformation() throws IOException {
		Log.e("NetworkController", "sending player information ......");
		MessageWriter message = new MessageWriter();
		message.writeByte((byte) kMessagePlayerInformation);
		// 플레이어가 보내는 정보
		message.writeString(kTempFacebookId);
		message.writeString(kTempName);
		message.writeByte((byte) kTempLevel);
		
		sendData(message.data_);
		setMessage(kMessagePlayerInformation, kModeSent);
		Log.e("NetworkController", "sendPlayerInformation");
	}
//
//	//private static void sendRequestMatch() throws IOException {
//	public static void sendRequestMatch() throws IOException {
//		Log.e("NetworkController", "sending request match ......");
//		MessageWriter message = new MessageWriter();
//		message.writeByte((byte) kMessageRequestMatch);
//		sendData(message.data_);
//	}
//	
	public void sendRequestIsPlayerConnected(String facebookId) throws IOException {
		Log.e("NetworkController", "send Request Is Player Connected ......");
		MessageWriter message = new MessageWriter();
		message.writeByte((byte) kMessageRequestIsPlayerConnected);
		message.writeString(facebookId);
		sendData(message.data_);
		setMessage(kMessageRequestIsPlayerConnected, kModeSent);
	}
	
	boolean owner = false;
	public void sendRoomOwner(int Boolean) throws IOException {
		Log.e("NetworkController", "sending sendRoomOwner ......");
		MessageWriter message = new MessageWriter();
		message.writeByte((byte) kMessageInRoomOwner);
		if (Boolean > 0) {
			message.writeByte((byte) 1);
			owner = true;
//			UserData.share(CCDirector.sharedDirector().getActivity()).iso
//			GameRandom.isOwner = true; // gamerandom 임시로 막음
//			GameInvite.getInstance().isOwner = true; // gameinvite 임시로 막음
		} else {
			message.writeByte((byte) 0);
			owner = false;
//			GameRandom.isOwner = false; // gamerandom 임시로 막음
//			GameInvite.getInstance().isOwner = false; // gameinvite 임시로 막음
		}
		sendData(message.data_);
		setMessage(kMessageInRoomOwner, kModeSent);
		Log.e("NetworkController", "sendRoomOwner");
	}
	
//	public static void sendRequestMatchInvite(int difficulty, String facebookID) {
//		Log.e("NetworkController", "send Request Match Invite ......");
//		MessageWriter message = new MessageWriter();
//		message.writeByte((byte) kMessageRequestMatch);
//		message.writeByte((byte) difficulty);
//		message.writeString(facebookID);
//		sendData(message.data_);
//		Log.e("NetworkController", "sendRequestMatch");
//	}
	
	public void sendRequestMatch(int difficulty) throws IOException {
		Log.e("NetworkController", "sending request match ......");
		MessageWriter message = new MessageWriter();
		message.writeByte((byte) kMessageRequestMatch);
		message.writeByte((byte) difficulty);
		sendData(message.data_);
		Log.e("NetworkController", "sendRequestMatch");
	}
	
	// 형식으로 해당 페이스북 아이디 플레이어가 접속중 여부를 알려달라는 요청을 보낸다.
	private void sendRequestIsPlayerConntected (String	facebookID) throws IOException {
		MessageWriter message = new MessageWriter();
		
		message.writeByte((byte) kMessageRequestIsPlayerConnected);
		message. writeString(facebookID);
		
		this.sendData(message.data_);
		this.setMessage(kMessageRequestIsPlayerConnected, kModeSent);
		Log.e("NetworkController", "sendRequestIsPlayerConntected");
		
	}

	 // 해당 페이스북아이디의 플레이어와 매치를 이루어 달라는 요청을 보낸다.
	public void sendRequestMatchInvite(int Difficulty, String facebookID) throws IOException {
		MessageWriter message = new MessageWriter();
		message.writeByte((byte) kMessageRequestMatchInvite);
		message.writeByte((byte) Difficulty);
		message. writeString(facebookID);
		this.sendData(message.data_);
		this.setMessage(kMessageRequestMatchInvite, kModeSent);
		Log.e("NetworkController", "send Request Match Invite");
	}
	

	 // 해당 페이스북아이디의 플레이어와 매치를 이루어 달라는 요청을 보낸다.
	public void sendResponseMatchInvite(int isInvite, String facebookID) throws IOException {
		MessageWriter message = new MessageWriter();
		
		message.writeByte((byte) kMessageResponseMatchInvite);
		message.writeByte((byte) isInvite); // 0은 거부 1은 승인
		message. writeString(facebookID);
		
		this.sendData(message.data_);
		this.setMessage(kMessageResponseMatchInvite, kModeSent);
		Log.e("NetworkController", "kMessageResponseMatchInvite");
	}
	
	public void sendWillYouAcceptInviteOk(String facebookID) throws IOException{
		MessageWriter message = new MessageWriter();
		
		message.writeByte((byte) kMessageWillYouAcceptInviteOK);
		message. writeString(facebookID);
		
		this.sendData(message.data_);
		this.setMessage(kMessageWillYouAcceptInviteOK, kModeSent);
		Log.e("NetworkController", "kMessageWillYouAcceptInviteOK");

	}
	 
	
	
	
	 // 게임 플레이어 데이타 보내기
	private void sendPlayData(int type, int value) throws IOException {
		MessageWriter message = new MessageWriter();
		
		message.writeByte((byte) kMessagePlayData);
		message.writeByte((byte) type);
		message.writeInt(value);
		
		this.sendData(message.data_);
		this.setMessage(kMessagePlayData, kModeSent);
		Log.e("NetworkController", "sendPlayData");
	}

//	 MineCell.open()
	// private void sendPlayDataCellOpen(int cell_ID) throws IOException {
	public void sendPlayDataCellOpen(int cell_ID) throws IOException {
		this.sendPlayData(kPlayDataCellOpen, cell_ID);
	}
//	
////	 MineCell.open()
//	//private void sendPlayDataMine(int cell_ID) throws IOException {
//	public void sendPlayDataMine(int cell_ID) throws IOException {
//		this.sendPlayData(kPlayDataMine, cell_ID);
//	}
	
//	
//	// Game.handleLongPress()
//	// private void sendPlayDataMushroomOff(int cell_ID) throws IOException {
//	public void sendPlayDataMushroomOff(int cell_ID) throws IOException {
//		this.sendPlayData(kPlayDataMushroomOff, cell_ID);
//	}
////	 Game.handleLongPress()
//	// private void sendPlayDataMushroomOn(int cell_ID) throws IOException {
//	public void sendPlayDataMushroomOn(int cell_ID) throws IOException {
//		this.sendPlayData(kPlayDataMushroomOn, cell_ID);
//	}
//	
//	 Game.addSphereTo()
	// 아이템 타입추가 필요
	//private void sendPlayDatasphereTake(int cell_ID) throws IOException {
	public void sendPlayDatasphereTake(int cell_ID) throws IOException {
		this.sendPlayData(kPlayDataSphereTake, cell_ID);
	}
//	 Game.addSphereTo()
	// 아이템 타입추가 필요
	//private void sendPlayDataSphere(int cell_ID) throws IOException {
	public void sendPlayDataSphere(int cell_ID) throws IOException {
		this.sendPlayData(kPlayDataSphere, cell_ID);
	}
//	 Game.gameOver()
	//private void sendPlayDataGameOver(int point) throws IOException {
	public void sendPlayDataGameOver(int point) throws IOException {
		this.sendPlayData(kPlayDataGameOver, point);
	}
//	HudLayer.clicked()
	// private void sendPlayDataMagicAttack(int magicType) throws IOException {
	public void sendPlayDataMagicAttack(int magicType) throws IOException {
		this.sendPlayData(kPlayDataMagicAttack, magicType);
	}
//	HudLayer.clicked()
	// private void sendPlayDataMagicDefense(int magicType) throws IOException {
	public void sendPlayDataMagicDefense(int magicType) throws IOException {
		this.sendPlayData(kPlayDataMagicDefense, magicType);
	}
	//GameEmoticon.clicked()
	//private void sendPlayDataEmoticon(int emoticon_ID) throws IOException {
		public void sendPlayDataEmoticon(int emoticon_ID) throws IOException {
		this.sendPlayData(kPlayDataEmoticon, emoticon_ID);
	}


	//
	// 현재, 딜리게이트는 테스트용 메시지 출력을 위해 사용되는데 지나지 않는다.
	static interface NetworkControllerDelegate{
		// 네트워크 상황 변동시 호출되도록 한다.
		public void stateChanged(int networkState);
		// 메시지 보낼 때 호출 되도록 한다.
		public void messageSent(int messageType);
		// 메시지 받았을 때 호출 되도록 한다.
		public void messageReceived(int messageType);
		// 세부사항을 설정 하도록 한다.
		public void setDetails(String message);
		
	}
	

	final String randomfolder = "52random/";
	CCSprite counter = null;
	int count  = 5;
	private void count(CCSprite parentSprite){
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		CCSprite tornado = CCSprite.sprite(randomfolder + "Tornado.png");
//		tornado.setAnchorPoint(0.5f, 0.5f);
		tornado.setPosition(parentSprite.getContentSize().width/2, parentSprite.getContentSize().height * 0.28f);
		parentSprite.addChild(tornado);
		CCRepeatForever repeat = CCRepeatForever.action(CCRotateBy.action(16, 360));
		tornado.runAction(repeat);
		
		counter = CCSprite.sprite(randomfolder + "n05.png");
		counter.setPosition(tornado.getPosition());
		parentSprite.addChild(counter);

		MainApplication.getInstance().mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {countdown(1000);
			}
		}, 1000);
	}
	
	public void countdown(long time) {
		try {
			boolean isLoop = true;
			while (isLoop) {
				Thread.sleep(time);
				count--;
				if (count > 0) {
					CCSprite counterNumber = CCSprite.sprite(randomfolder
							+ "n0" + count + ".png");
					counter.setTexture(counterNumber.getTexture());
				} else {
					isLoop = false;
				}
			}
			// 작업하지않는 다른 패키지 이지만 잘 붙는지만 확인하는 것입니다.
			CCDirector.sharedDirector().replaceScene(Game.scene());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
