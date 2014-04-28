package com.aga.mine.mains;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import android.util.Log;

import com.aga.mine.mains.ImageDownloader.ImageLoaderListener;
import com.aga.mine.mains.NetworkController.MatchCallback;
import com.aga.mine.util.Util;

public class GameInvite extends CCLayer {
	
	// config 파일에 나중에 옮길것
	public static boolean buttonActive = true;
	final int previous = 501;
	final int home= 502;
	
	final String commonfolder = "00common/";
	final String folder = "53invite/";
	final int singleMode = 1;
	final int randomMode = 2;
	final int inviteMode = 3;
	
	String matchedOppenentFacebookId;
	String matchedOppenentName;
	public boolean isOwner = false;
	
	private CCSprite bg;
	static CCSprite backboard;

    private static CCScene scene;
//	private static GameInvite gameInvite;
	
	public static CCScene scene() {
		scene = CCScene.node();
		CCLayer layer = new GameInvite();
		scene.addChild(layer);
		return scene;
	}
	
	public static CCScene scene(String id, String name, boolean owner) {
		scene = CCScene.node();
		CCLayer layer = new GameInvite(id, name, owner);
		scene.addChild(layer);
		return scene;
	}
	
//	public static synchronized GameInvite getInstance() {
//		if (gameInvite == null)
//			gameInvite = new GameInvite();
//		return gameInvite;
//	} // 불필요 할듯

	private GameInvite() {
		isOwner = NetworkController.getInstance().owner;
		
		//when invitation is successful, this callback is called.
    	Log.e("Invite", "Callback_1 - setInviteCallback()");
//		MainApplication.getInstance().getActivity().setInviteCallback(mInviteCallback);
		NetworkController.getInstance().setMatchCallback(mMatchCallback);
		//배경 그림 설정
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1.png");	
		setBackBoardMenu(commonfolder + "bb1.png");
		setBoardFrameMenu(commonfolder + "frameMatching-hd.png");
//		Util.setEntry(null, null, true, backboard);
		// 상단 메뉴
		TopMenu2.setSceneMenu(this);
		// 하단 이미지
		BottomMenu3.setBottomMenu(null, folder, this); 
		
////		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
//		//display scroll view
//		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_DISPLAY_MATCHLIST);
	}
	
	String oppenentId;
	String oppenentName;
	private GameInvite(String id, String name, boolean owner) {
		oppenentId = id;
		oppenentName = name;
		isOwner = owner;
		//배경 그림 설정
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1.png");	
		setBackBoardMenu(commonfolder + "bb1.png");
		setBoardFrameMenu(commonfolder + "frameMatching-hd.png");
//		Util.setEntry(id, name, owner, backboard);
		// 상단 메뉴
		TopMenu2.setSceneMenu(this);
		
		// 하단 이미지
		BottomMenu3.setBottomMenu(null, folder, this); 
	}
	
	

	// 백 보드 설정
	private void setBackBoardMenu(String imageFullPath) {
		backboard = CCSprite.sprite(imageFullPath);
		bg.addChild(backboard, 0, 0);
		backboard.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		backboard.setAnchorPoint(0.5f, 0.5f);
		setMainMenu(backboard);
	}
	
	static CCSprite boardFrame;
	// 게시판 설정
	private void setBoardFrameMenu(String imageFullPath) {
		boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		FrameTitle2.setTitle(boardFrame, folder);
	}
	
	// 중복코드 제거용으로 만들었지만 잘 안되서 사용 안함.
//	private CCSprite spriteSetTexture(String id) {
//		final CCSprite _picture = null;
//		String etUrl = "https://graph.facebook.com/" + id + "/picture";
//		mDownloader = new ImageDownloader(etUrl, new ImageLoaderListener() {
//			@Override
//			public void onImageDownloaded(CCSprite profile) {
//				_picture.setTexture(profile.getTexture());
//			}
//		});
//		mDownloader.execute();
//		return _picture;
//	}
	
//	List<CCNode> matchingPanel = null;
	// 메인 메뉴
	private void setMainMenu(CCSprite parentSprite){
		
		List<CCNode> matchingPanel = new ArrayList<CCNode>();
		matchingPanel.add(CCSprite.sprite(commonfolder + "matchPanelMe.png")); 
		matchingPanel.add(CCSprite.sprite(commonfolder + "matchPanelOther.png")); 
				
		int count = 1;
		for (CCNode panel : matchingPanel) {
			String name;

			parentSprite.addChild(panel, 0, count);
			panel.setPosition(
					parentSprite.getContentSize().width / 2, 
					parentSprite.getContentSize().height - panel.getContentSize().height * (count * 1.05f + 0.1f));
			
			// 이미지
			final CCSprite _picture = CCSprite.sprite(commonfolder + "noPicture.png"); // 프로필 사진		
			_picture.setAnchorPoint(0.5f, 0.5f);
			_picture.setPosition(_picture.getContentSize().width * 2.7f, panel.getContentSize().height / 2);
			panel.addChild(_picture, 0, 102);
			
			if (isOwner) {
				name = FacebookData.getinstance().getUserInfo().getName();
				String etUrl = "https://graph.facebook.com/" + FacebookData.getinstance().getUserInfo().getId() + "/picture";
				mDownloader = new ImageDownloader(etUrl, new ImageLoaderListener() {
					@Override
					public void onImageDownloaded(CCSprite profile) {
						_picture.setTexture(profile.getTexture());
					}
				});
				mDownloader.execute();
			} else if (oppenentId != null && oppenentName != null) {
				Util.count(backboard); // 이미지 로드전에 카운트다운부터 도는게 맞는데 이미지가 로드가 안되네... 흠.
				name = oppenentName;
				String etUrl = "https://graph.facebook.com/" + oppenentId + "/picture";
				mDownloader = new ImageDownloader(etUrl, new ImageLoaderListener() {
					@Override
					public void onImageDownloaded(CCSprite profile) {
						Log.e("GameInvite", "onImageDownloaded - MainThread");
						_picture.setTexture(profile.getTexture());
					}
				});
				mDownloader.execute();
//				Util.count(backboard);
			} else {
				name = "Player" + count;
			}
			

			
//			if (oppenentId != null && oppenentName != null) {
//				name = oppenentName;
//				String etUrl = "https://graph.facebook.com/" + oppenentId + "/picture";
//				mDownloader = new ImageDownloader(etUrl, new ImageLoaderListener() {
//					@Override
//					public void onImageDownloaded(CCSprite profile) {
//						Log.e("GameInvite", "onImageDownloaded - MainThread");
//						_picture.setTexture(profile.getTexture());
//					}
//				});
//				mDownloader.execute();
//				isOwner = false;
//				oppenentId = null;
//				oppenentName = null;
//			} else {
//				name = "Player" + count;
//			}
//			
//			if (isOwner) {
//				name = FacebookData.getinstance().getUserInfo().getName();
//				String etUrl = "https://graph.facebook.com/" + FacebookData.getinstance().getUserInfo().getId() + "/picture";
//				mDownloader = new ImageDownloader(etUrl, new ImageLoaderListener() {
//					@Override
//					public void onImageDownloaded(CCSprite profile) {
//						_picture.setTexture(profile.getTexture());
//					}
//				});
//				mDownloader.execute();
//			}
			
			// 이름
			CCLabel _name = CCLabel.makeLabel(name, "Arial", 30.0f);
			_name.setAnchorPoint(0, 0.5f);
			_name.setPosition(
					_picture.getPosition().x + ((1.3f - _picture.getAnchorPoint().x) * _picture.getContentSize().width), 
					_picture.getPosition().y);
			panel.addChild(_name, 0, 103);
			count ++;
			isOwner = !isOwner;
		}
	}
		

	
	// sceneCallback들 전부 여기로 옮기기
	public void clicked(Object sender) {
		// hide scroll view
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
		CCScene scene = null;
		int value = ((CCNode) sender).getTag();
		if (buttonActive) {
			GameData.share().setGameDifficulty(0);

			switch (value) {
			case previous:
				scene = GameDifficulty.scene();
				
				break;

			case home:
				scene = Home.scene();
				break;
			}
			try {
				NetworkController.getInstance().sendRoomOwner(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			CCDirector.sharedDirector().replaceScene(scene);
		}
	}

	// BottomMenu3.setBottomMenu
	public void randomMatch(Object sender) {
		// hide scroll view
		MainApplication.getInstance().getActivity().mHandler.sendEmptyMessage(Constant.MSG_HIDE_SCROLLVIEW);
//		try {
////			GameDifficulty.mode =2;
//			GameData.share().setGameMode(randomMode);
//			NetworkController.getInstance().sendRoomOwner(1); // 방장 권한 주입 (random match에서만 있음)
//			CCScene scene = GameRandom.scene();
//			CCDirector.sharedDirector().replaceScene(scene);
//			Log.e("CallBack", "RandomMatchLayer");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		try {
			NetworkController.getInstance().sendRequestMatch(GameData.share().getGameDifficulty()); // 난이도 주입
			NetworkController.getInstance().sendRoomOwner(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (CCNode child : this.getChildren()) {
			Log.e("GameInvite", child.toString());
		}
	}
	
	// ok!
	private static ImageDownloader mDownloader;
	public MatchCallback mMatchCallback = new MatchCallback() {

		@Override
		public void onMatch(String matchedOppenentFacebookId, String matchedOppenentName, boolean owner) {
			Util.count(backboard); // 상대방이 방장(나를 초대한 사람)일시 이미지가 로드가 잘 안됨
			final CCSprite picture;
			CCLabel name;
			
			if (!owner) {
				picture = (CCSprite) backboard.getChildren().get(0).getChildByTag(102);
				name = (CCLabel) backboard.getChildren().get(0).getChildByTag(103);
			} else {
				picture = (CCSprite) backboard.getChildren().get(1).getChildByTag(102);
				name = (CCLabel) backboard.getChildren().get(1).getChildByTag(103);
			}
			
			String etUrl = "https://graph.facebook.com/" + matchedOppenentFacebookId + "/picture";
			name.setString(matchedOppenentName);
			
			mDownloader = new ImageDownloader(etUrl, new ImageLoaderListener() {
				@Override
				public void onImageDownloaded(CCSprite profile) {
					picture.setTexture(profile.getTexture());
				}
			});
			mDownloader.execute();
//			Util.count(backboard);
		}

		@Override
		public void setOwner() {
			List<CCNode> entry = backboard.getChildren();
			CCSprite tempPicture = null;
			CCLabel tempName = null;
			
			int count = entry.size();
			for (CCNode ccNode : entry) {
				if (count > 0) {
					CCNode temp = entry.get(count);
					
					tempPicture = (CCSprite) ccNode.getChildByTag(102);
					((CCSprite) ccNode.getChildByTag(102)).setTexture(((CCSprite) temp.getChildByTag(102)).getTexture());
					
					tempName = (CCLabel) ccNode.getChildByTag(103);
					((CCLabel) ccNode.getChildByTag(103)).setString(((CCLabel) temp.getChildByTag(103)).getString());					
				} else {
					((CCSprite) ccNode.getChildByTag(102)).setTexture(tempPicture.getTexture());
					
					tempName = (CCLabel) ccNode.getChildByTag(103);
					((CCLabel) ccNode.getChildByTag(103)).setString(tempName.getString());				
				}
				count--;
			}
			
		}
		
	};
	
	
}