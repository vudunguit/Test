package com.aga.mine.pages2;

import java.io.IOException;

import org.cocos2d.actions.base.CCFiniteTimeAction;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.util.Log;
import android.view.MotionEvent;

import com.aga.mine.mains.Config;
import com.aga.mine.mains.NetworkController;
import com.aga.mine.mains.Utility;


// �̹��� ���ϸ��� -hd�� ���� �Ͽ��� ���� �߻� ��.
// �Ϸ��� ���ϸ� �����Ұ�
public class GameEmoticon extends CCLayer{

	String folder = "62game_emoticon/";
	static final int kButtonBase = 0;
	static final int kButtonSend = 1;
	CGPoint basePositionOn;
	CGPoint basePositionOff;
	CCSprite base;
	boolean isEmoticonPanelOn;
	
	public GameEmoticon() {
		layout();
	}

	private void layout() {
//		CCTouchDispatcher.sharedDispatcher().addTargetedDelegate(this, 0, true);
//		this.setIsTouchEnabled(true); 

		CGSize winSize = CCDirector.sharedDirector().winSize();
		isEmoticonPanelOn = false;
		
		//
		// ���̽�
		base = CCSprite.sprite(folder + "emoticon-base.png");
		this.addChild(base);
		CGSize baseActive = CGSize.make(
				base.getContentSize().width - base.getContentSize().width / 7.7f, 
				base.getContentSize().height);
		
		//
		// �� ��ư
		CCMenuItem item = CCMenuItemImage.item(
				folder + "emoticon-baseButton-hd.png",
				folder + "emoticon-baseButton-hd.png",
				this, "clicked");
		item.setTag(kButtonBase);
		CCMenu baseButton = CCMenu.menu(item);
		base.addChild(baseButton);
		baseButton.setPosition(
				base.getContentSize().width - item.getContentSize().width /2,
				base.getContentSize().height /2 + 5f);
		basePositionOn = CGPoint.ccp(winSize.width / 2, winSize.height /2);
		basePositionOff = CGPoint.ccp(
				basePositionOn.x - base.getContentSize().width + item.getContentSize().width + 5, 
				basePositionOn.y);
		
		base.setPosition(GameConfig.share().isEmoticonPanelOn ? basePositionOn : basePositionOff);
		
		//
		// ����
		String titleFile = Utility.getInstance().getNameWithIsoCodeSuffix(folder + "emoticon-title.png");
		CCSprite title = CCSprite.sprite(titleFile);
		base.addChild(title);
		title.setPosition(baseActive.width / 2, baseActive.height - title.getContentSize().height * 1.2f);
		
		//
		// �̸�Ƽ�ܵ�
		CCSprite emoticonBase = CCSprite.sprite(folder + "emoticon-tiles.png");
		base.addChild(emoticonBase);
		emoticonBase.setPosition(baseActive.width / 2, baseActive.height / 2 + baseActive.height / 50);
		
		// �̸�Ƽ�� �� ��ġ�� Ȯ��
		CGPoint basePosition = CGPoint.ccp(baseActive.width / 11f, baseActive.height / 3.5f);
		float cellSize = 38f * 2; // 128pixel ����(���� 64pixel �����) 
		for (int i = 0; i < 7; i++) {
			for (int k = 0; k < 4; k++) {
				CCLabel mark = CCLabel.makeLabel("+", "Arial", 15);
				base.addChild(mark);
				mark.setPosition(basePosition.x + cellSize * i, basePosition.y + cellSize * k);
				mark.setTag(i*10+k);
			}
		}
		
		//
		// �Է�â
		CCSprite inputBase = CCSprite.sprite(folder + "emoticon-inputBase-hd.png");
		base.addChild(inputBase);
		inputBase.setPosition(baseActive.width/2 , inputBase.getContentSize().height * 0.9f);
		
		//
		// ������ ��ư
		CCMenuItem itemSend = CCMenuItemImage.item(
				folder + "emoticon-buttonSend-hd.png", 
				folder + "emoticon-buttonSendOver-hd.png",
				this, "clicked");
		itemSend.setTag(kButtonSend);
		CCMenu buttonSend = CCMenu.menu(itemSend);
		inputBase.addChild(buttonSend);
		buttonSend.setPosition(
				inputBase.getContentSize().width - itemSend.getContentSize().width * 0.6f, 
				itemSend.getContentSize().height * 0.6f);
		
		//
		// ��ư ����
		// ��� �ľ��� ���ϸ� �ٿ��� �����ϱ�
		//String textFile = Utility.getInstance().getNameWithIsoCodeSuffix("emoticon-textSendKo-hd.png");
		String textFile = folder + Utility.getInstance().getNameWithIsoCodeSuffix("emoticon-textSend.png");
		CCSprite textSend = CCSprite.sprite(textFile);
		itemSend.addChild(textSend);
		textSend.setPosition(
				itemSend.getContentSize().width / 2, 
				itemSend.getContentSize().height / 2);
	}
	// layout() end

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		return true;
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		return super.ccTouchesEnded(event);
	}
	
	
	//public void clicked(CCMenuItem button) {
	public void clicked(Object sender) {
		//button clicked
		Log.e("GameEmoticon / clicked", "clicked");
		
		switch (((CCMenuItem)sender).getTag()) {
		case kButtonBase:
			Log.e("clicked", "kButtonBase clicked");

			if (GameConfig.share().isMinimapPanelOn == false) {
				
				// �̸�Ƽ�� �г� ��/����
				isEmoticonPanelOn = !isEmoticonPanelOn;
				GameConfig.share().setEmoticonPanelOn(isEmoticonPanelOn);
				
				// ��/���� �ִϸ��̼�
				CCFiniteTimeAction moveOn = CCMoveTo.action(GameConfig.share().kEmoticonPanelMoveTime, basePositionOn);
				CCFiniteTimeAction moveOff = CCMoveTo.action(GameConfig.share().kEmoticonPanelMoveTime, basePositionOff);
				
				if(isEmoticonPanelOn) {
					Config.getInstance().setDisableButton(true);
					base.runAction(CCSequence.actions(moveOn));
				} else {
					Config.getInstance().setDisableButton(false);
					base.runAction(CCSequence.actions(moveOff));
				}
			}
			break;
		case kButtonSend:
			Log.e("clicked", "kButtonSend clicked");
			if (GameData.share().isMultiGame) {
				int EmoticonID = 1; // ���ڰ��� �̸�Ƽ�� ID���� �ִ´�.
				try {
					NetworkController.getInstance().sendPlayDataEmoticon(EmoticonID);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;

		default:
			break;
		}
		Log.e("Method", "clicked end");
	}
	// clicked() end

}
// class end
