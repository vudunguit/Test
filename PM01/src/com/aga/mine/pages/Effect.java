package com.aga.mine.pages;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCFiniteTimeAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.instant.CCCallFuncND;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;

public class Effect extends CCLayer{

	private static Effect effect;
	
	public static synchronized Effect share() {
		if (effect == null) {
			effect = new Effect();
		}
		return effect;
	}
	
	private Effect() {
	}

	public void pumpkinExplosion(CCNode parent, CGPoint position) {
		CCSprite sprite = this.animationUniveral("fxPumpkinExplosion.plist", 9, 0.05f);
		sprite.setPosition(CGPoint.ccpAdd(position, CGPoint.ccp(-0.5f, -0.3f)));
		parent.addChild(sprite);
	}
	private CCSprite animationUniveral(String plistName, int numberOfFrame, float frameTime) {
		return this.animationUniveral(plistName, numberOfFrame, frameTime, false);
	}
	private CCSprite animationUniveral(String plistName, int numberOfFrame, float frameTime, boolean isRepeat) {
		return this.animationUniveral(plistName, numberOfFrame, frameTime, isRepeat, null);
	}
	private CCSprite animationUniveral(String plistName, int numberOfFrame, float frameTime, boolean isRepeat, String prefix) {
		return animationUniveral(plistName, numberOfFrame, frameTime, isRepeat, prefix, 0, 0);
	}
	private CCSprite animationUniveral(String plistName, int numberOfFrame, float frameTime, 
			boolean isRepeat, String prefix, float delayBefore, float delayAfter) {
		CCSpriteFrameCache.sharedSpriteFrameCache().addSpriteFrames(plistName);
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		DecimalFormat df = new DecimalFormat("00");
		for (int i = 0; i < numberOfFrame; i++) {
			String frameName = prefix + df.format(i+1) + ".png"; // prefix null exception 뜰수 있음. (뜨면 3항연산으로 처리할것)
			frames.add(CCSpriteFrameCache.sharedSpriteFrameCache().spriteFrameByName(frameName));
		}
		// id가 뭔지 모르겠다. 
		CCDelayTime before = CCDelayTime.action(delayBefore);
		CCDelayTime after = CCDelayTime.action(delayAfter);
		CCAnimation animation = CCAnimation.animation("pumpkinExplosion", frameTime, frames	);
		CCAnimate animate = CCAnimate.action(animation);
		String frameName = prefix + "01.png";
		CCSprite sprite = CCSprite.sprite(frameName);
		sprite.setTag(kTagGrossini);
		// CCCallFuncND 이거 잘 될지 모르겠음.
		// parent 
		CCCallFunc removeMySprite =  CCCallFunc.action(this, "removeAction"); // this 또는 nodeThis
//		CCCallFunc removeMySprite1 =  CCCallFunc.action(sprite, "removeFromParentAndCleanup");
//		CCCallFuncN removeMySprite2 =  CCCallFuncN.action(sprite, "removeFromParentAndCleanup", null);
//		CCCallFuncND removeMySprite =  CCCallFuncND.action(sprite, "removeFromParentAndCleanup", null);
		if (isRepeat) {
			CCSequence sequence = CCSequence.actions(before, animate, after);
			CCRepeatForever repeat = CCRepeatForever.action(sequence);
			sprite.runAction(repeat);
		} else {
			CCSequence sequence = CCSequence.actions(before, animate, after, removeMySprite);
			sequence.setTag(kTagsequence);
			sprite.runAction(sequence);
		}
		return sprite;
	}
	
	int kTagsequence = 987;
	int kTagGrossini = 254;
	
	private void removeAction() {
		CCNode sprite = this.getChildByTag(kTagGrossini);
		sprite.stopAction(kTagsequence);
	}
	
	public void cellOpen(Game game, CGPoint getTilePosition, int globalIndex) {
		// TODO Auto-generated method stub
		
	}

	

}
