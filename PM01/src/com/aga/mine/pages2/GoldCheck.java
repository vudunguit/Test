package com.aga.mine.pages2;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGSize;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.aga.mine.mains.Home;

public class GoldCheck {
	
	//CCLayer layer;
	private Context mContext;
	static UserData userData;
	
	public GoldCheck() {
		mContext = CCDirector.sharedDirector().getActivity();
		userData = UserData.share(mContext);
	}

	private static CGSize winsize() {
		return CCDirector.sharedDirector().winSize();
	}
	
	public static void goldcheck(final CCLayer layer, int gold) {
	
		if (userData.getGold() < gold) {

			CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
						public void run() {
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
//							Home.buttonActive = false;

							CCSprite child = CCSprite
									.sprite("opacityBackground.png");
							child.setPosition(winsize().width / 2,
									winsize().height / 2);

							CCSprite board = CCSprite
									.sprite("giftboxBackboard.png");
							board.setPosition(child.getContentSize().width / 2,
									child.getContentSize().height / 2);

							CCSprite title = CCSprite
									.sprite("giftboxTitleKo.png");
							title.setPosition(
									board.getContentSize().width / 2,
									board.getContentSize().height
											- title.getContentSize().height
											* 0.95f);

							CCMenuItem pester = CCMenuItemImage.item(
									"pesterButtonKoNormal.png",
									"pesterButtonKoPress.png", layer,
									"pesterCallback");

							CCMenuItem present = CCMenuItemImage.item(
									"presentButtonKoNormal.png",
									"presentButtonKoPress.png", layer,
									"presentCallback");

							CCMenuItem[] menu = { pester, present };
							CCMenu giftMenu = CCMenu.menu(menu);

							giftMenu.setContentSize(
									board.getContentSize().width,
									board.getContentSize().height);
							giftMenu.setPosition(0f, 0f);

							pester.setPosition(
									pester.getContentSize().width * 0.75f,
									giftMenu.getContentSize().height / 2
											- pester.getContentSize().height
											* 0.2f);
							present.setPosition(giftMenu.getContentSize().width
									- present.getContentSize().width * 0.75f,
									giftMenu.getContentSize().height / 2
											- present.getContentSize().height
											* 0.2f);
		

							child.addChild(board);
							board.addChild(title);
							board.addChild(giftMenu);

							layer.addChild(child, 900, 900);

							Log.e("gold end", "gold end1");
						}
					});
		}
		// if end
		Log.e("gold end", "gold end2");
	}
	// goldcheck() end
	
	public void pesterCallback(final Object sender) {



				Log.e("gold end", "gold end3");

//				Home.buttonActive = true;
				CCLayer targetLayer = (CCLayer) sender;
				targetLayer.removeChildByTag(900, true);

				Log.e("gold end", "gold end4");

				Toast.makeText(mContext, "친구에게 조르기 도전!!", Toast.LENGTH_SHORT)
						.show();


	}

	public void presentCallback(final Object sender) {


				Log.e("gold end", "gold end3");

//				Home.buttonActive = true;
				CCLayer targetLayer = (CCLayer) sender;
				targetLayer.removeChildByTag(900, true);

				Log.e("gold end", "gold end4");

				// GoldCallback(mContext);
				//CCScene scene = GoldLayer.getInstance(mContext).scene(mContext);
				//CCDirector.sharedDirector().replaceScene(scene);

	}

}
// end
