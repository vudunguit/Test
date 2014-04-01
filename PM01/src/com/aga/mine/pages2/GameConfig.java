package com.aga.mine.pages2;

// �ϴ� ����� ���� �Ϸ�
public class GameConfig {

	//
	// ���� ���� �Ϲ�
	final float kLongPressTime = 0.8f; // �������� �ߵ��ð�
	final float kDefaultScale = 0.71f; // ó�� �� ������, .23f for full screen

	// ���� . �ƿ� ����
	final float kPinchZoomMultiplier = 0.005f;
	//final float kPinchZoomMultiplier = 0.0015f;
	final float kMinScale = 0.23f;
	final float kMaxScale = 1.0f;

	// �̸�Ƽ�� �г� �̵� �ð�
	final float kEmoticonPanelMoveTime = 0.2f;

	//
	// ���̾� ����, �޴� ���
	final int kDepthUI = 0;
	final int kDepthPopup = 20;
	int kDepthMagician = 10;

	boolean isEmoticonPanelOn;
	boolean isMinimapPanelOn;
	boolean isEndingOn;

	//---------------------------------------------------------------//
	private static GameConfig gameConfig;
	
	private GameConfig() {
	}

	// #pragma mark - Singleton Methods
	public static synchronized GameConfig share() {
		if (gameConfig == null) {
			gameConfig = new GameConfig();
		}
		return gameConfig;
	}
	//---------------------------------------------------------------//

	public boolean isEmoticonPanelOn() {
		return isEmoticonPanelOn;
	}

	public void setEmoticonPanelOn(boolean isEmoticonPanelOn) {
		this.isEmoticonPanelOn = isEmoticonPanelOn;
	}

	public boolean isMinimapPanelOn() {
		return isMinimapPanelOn;
	}

	public void setMinimapPanelOn(boolean isMinimapPanelOn) {
		this.isMinimapPanelOn = isMinimapPanelOn;
	}

	public boolean isEndingOn() {
		return isEndingOn;
	}

	public void setEndingOn(boolean isEndingOn) {
		this.isEndingOn = isEndingOn;
	}
	
	
}
// end