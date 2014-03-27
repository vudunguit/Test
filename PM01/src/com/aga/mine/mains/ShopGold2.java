package com.aga.mine.mains;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

import com.aga.mine.pages.UserData;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

public class ShopGold2 extends CCLayer {
	
	final String commonfolder = "00common/";
	final String folder = "21gold/";
	final String fileExtension = ".png";
	
	CCSprite bg;
//	CCLabel gold = null; 
	
	double[][] goldArray = { 
			{ 0.99, 5000, 0 }, { 4.99, 25000, 6250 },
			{ 9.99, 50000, 15000 }, { 24.99, 125000, 43750 },
			{ 49.99, 250000, 100000 }, { 99.99, 500000, 225000 } };
	
	private Context mContext;
	UserData userData;
	
	static CCScene scene() {
		CCScene scene = CCScene.node();
		CCLayer layer = new ShopGold2();
		scene.addChild(layer);
//		scene.addChild(InvitationReceiver.getInstance().getInvitationPopup());
		return scene;
	}

	public ShopGold2() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
		
		// BackGround ����
		bg = BackGround.setBackground(this, CGPoint.make(0.5f, 0.5f), commonfolder + "bg1" + fileExtension);
		setBackBoardMenu(commonfolder + "gamebb" + fileExtension);
		setBoardFrameMenu(commonfolder + "frameGeneral-hd" + fileExtension);
		
		TopMenu2.setSceneMenu(this);
		BottomImage.setBottomImage(this);
		
		this.setIsTouchEnabled(true);
	}
	
	private void setBackBoardMenu(String imageFullPath) {
		CCSprite bb = CCSprite.sprite(imageFullPath);
		bg.addChild(bb);
		bb.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		bb.setAnchorPoint(0.5f, 0.5f);
		setMainMenu(bb);
	}
	
	private void setBoardFrameMenu(String imageFullPath) {
		CCSprite boardFrame = CCSprite.sprite(imageFullPath);
		bg.addChild(boardFrame);
		boardFrame.setPosition(bg.getContentSize().width / 2, bg.getContentSize().height * 0.525f);
		boardFrame.setAnchorPoint(0.5f, 0.5f);
		
		FrameTitle6.setTitle(boardFrame, folder);
//		gold = FrameTitle6.setTitle(boardFrame, folder);
//		gold.setString("" + UserData.share(CCDirector.sharedDirector().getActivity()).getGold());
	}
	
	private void setMainMenu(CCSprite parentSprite){
		CCMenu gameMenu = CCMenu.menu(buttons(goldArray));
		gameMenu.alignItemsVertically(-1);
		gameMenu.setPosition(CGPoint.ccp(parentSprite.getContentSize().width / 2 - 6, parentSprite.getContentSize().height / 2 - 13));
		parentSprite.addChild(gameMenu);
	}
	
	private CCMenuItem[] buttons(double[][] valuesArray) {
		List<CCMenuItem> listMenus = new ArrayList<CCMenuItem>();
		
		for (double[] ds : valuesArray) {
			CCMenuItemImage button = CCMenuItemImage.item(
					folder + "buttonnormal" + fileExtension,
					folder + "buttonpressed" + fileExtension,
					this, "buttonCallback");
			button.setUserData(ds[0]);
			
			// ��� �̹���
			CCSprite goldimage = CCSprite.sprite(folder + (int)ds[1] + fileExtension);
			goldimage.setPosition(goldimage.getContentSize().width / 2, button.getContentSize().height / 2);
			button.addChild(goldimage);
			
			// �ξ� ����
			CCLabel  usd = CCLabel.makeLabel("$ " + ds[0], "Arial", 26);
			usd.setColor(ccColor3B.ccYELLOW);
			usd.setAnchorPoint(1, 1);
			usd.setPosition(button.getContentSize().width - 15, button.getContentSize().height - 5);
			button.addChild(usd);
			
			// ���&�߰����
			CCLabel  value = CCLabel.makeLabel(plus((int)ds[1], (int)ds[2]), "Arial", 30);
			value.setAnchorPoint(1, 0);
			value.setPosition(button.getContentSize().width - 15, 10);
			button.addChild(value);
			
			//gold Text
			CCLabel  gold = CCLabel.makeLabel("Gold", "Arial", 18);
			gold.setColor(ccColor3B.ccYELLOW);
			gold.setAnchorPoint(value.getAnchorPoint());
			gold.setPosition(value.getPosition().x - value.getContentSize().width - 10, value.getPosition().y + 4);
			button.addChild(gold);
			
			listMenus.add(button);
		}
		
		CCMenuItem[] menusArray = listMenus.toArray(new CCMenuItem[listMenus.size()]);
		return menusArray;
	}
	
	private String plus	(int value1, int value2) {
		String values = numberComma(value1);
		if (value2 > 0)
			values = values + " + " + numberComma(value2);
		return values;
	}
	
	// int ���� String���� ��ȯ�Ͽ� comma ����
	private String Decimal(int value) {
		DecimalFormat format = new DecimalFormat("###,###,###,###");
		String ret = format.format(value);
		return ret;
	}
	
	// int ���� String���� ��ȯ�Ͽ� comma ����
	private String numberComma(int value) {
		String s = Integer.toString(value);
		return numberComma(s);
	}
	
	// String�� comma ����
	private String numberComma(String string) {
		if (string.length() < 4) 
			return string;
		
		String value = string;
		int len = value.length();
		int position = 3;
		for (int i = 0; i < (len -1) / 3; i++) {
			value = value.substring(0, (value.length() - position)) + "," + value.substring((value.length() - position), value.length());
			position += 4;
		}
		return value;
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		return super.ccTouchesEnded(event);
	}
	
	public void previousCallback(Object sender) {
		CCScene scene = Shop.scene();
		CCDirector.sharedDirector().replaceScene(scene);
		Log.e("CallBack", "ShopLayer");
	}

	public void homeCallback(Object sender) {
		CCScene scene = Home.scene();
		CCDirector.sharedDirector().replaceScene(scene);
		Log.e("CallBack", "HomeLayer");
	}
	
	public void buttonCallback(Object sender) {
		String Product = "gold";
		float bonus = 1.2f;
		double usd = (Double)((CCNode)sender).getUserData();
		int gold = 5000;
		
		for (int i = 0; i < goldArray.length; i++) {
			if (usd == goldArray[i][0]) {
				Product = Product + i;
				break;
			}
		}
		
		if (usd == goldArray[0][0]) {
			gold = (int)(gold * (usd +  0.01));
		} else {
			for (int i = 0; i < goldArray.length; i++) {
				if (usd == goldArray[i][0] ) {
					gold = (int) (gold * (usd +  0.01) * (bonus + (i * 0.05f)))   ;
					break;
				}
			}
		}

//		double requestID = Math.random() * 9223372036854775807L;  //facebook �˸��۹�ȣ�� ��ü�� ��
		long requestID = FacebookData.getinstance().getRequestID();  //test�� 
		String recipientID = FacebookData.getinstance().getRecipientID(); // ���� �̵� ��Ŀ� ���� ID ����
		String senderID = FacebookData.getinstance().getUserInfo().getId();
		String data = 
				"0,RequestModeMailBoxAdd*22," + requestID + 
				"*1," + recipientID + "*19," + senderID + "*20,Gold*21," + gold;
		FacebookData.getinstance().sendMail(data);
//		Passport.SKU = Product;
//		Log.e("ShopGold2", "buttonCallback(usd) : " + usd);
		
		
//		inAppBilling();
	}
		
	private void inAppBilling() {
//		Intent task1 = new Intent(CCDirector.sharedDirector().getActivity(), StartUpActivity.class);
//    	CCDirector.sharedDirector().getActivity().startActivity(task1);
	}
	
}
//end