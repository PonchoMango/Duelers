package view;

import controller.*;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import models.gui.*;
import models.languageLocalisation.LanguageData;
import models.message.OnlineGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static models.account.AccountType.ADMIN;

public class MainMenu extends Show {
    private static MainMenu menu;

    private final String playText = LanguageData.getInstance().getValue(new String[] {"MAIN_MENU", "PLAY"});
    private final String collectionText = LanguageData.getInstance().getValue(new String[] {"MAIN_MENU", "COLLECTION"});
    private final String chatText = LanguageData.getInstance().getValue(new String[] {"MAIN_MENU", "CHAT"});
    private final String spectateText = LanguageData.getInstance().getValue(new String[] {"MAIN_MENU", "SPECTATE"});
    private final String profileText = LanguageData.getInstance().getValue(new String[] {"MAIN_MENU", "PROFILE"});
    private final String quitText = LanguageData.getInstance().getValue(new String[] {"MAIN_MENU", "QUIT"});

    private final String logoutText = LanguageData.getInstance().getValue(new String[] {"PROFILE", "LOGOUT"});

    private final String onlineGamesText = LanguageData.getInstance().getValue(new String[]{"SPECTATE", "ONLINE_GAMES"});

    private static final Media backgroundMusic = new Media(
            new File("Client/resources/music/main_menu.m4a").toURI().toString()
    );
    private final List<MenuItem> items = new ArrayList<>();
    private int itemIndex = 0;
    private final MenuItem[] itemsArray = {
            new MenuItem(itemIndex++, playText,null, event -> PlayMenu.getInstance().show()),
            new MenuItem(itemIndex++, collectionText, null, event -> new CollectionMenu().show()),
            new MenuItem(itemIndex++, chatText, null, event -> GlobalChatDialog.getInstance().show()),
            new MenuItem(itemIndex++, spectateText, null, event -> showOnlineGamesList()),
            new MenuItem(itemIndex++, profileText, null, event -> menu.showProfileDialog()),
            new MenuItem(itemIndex++, quitText, null, event -> Client.getInstance().close()),

    };

    private DialogContainer onlineGamesDialog;

    {
        items.addAll(Arrays.asList(itemsArray));
        if (Client.getInstance().getAccount().getAccountType() == ADMIN) {
            System.out.println(Client.getInstance().getAccount().getUsername());
            items.addAll(Arrays.asList(
                    new MenuItem(itemIndex++, onlineGamesText, null, event -> menu.showOnlineGamesList())
            ));
        }
    }

    public static MainMenu getInstance() {
        return menu;
    }

    public MainMenu() {
        menu = this;
        try {
            root.setBackground(UIConstants.DEFAULT_ROOT_BACKGROUND);
            BorderPane background = BackgroundMaker.getMenuBackground();
            MainMenuBox menuBox = new MainMenuBox(items);

            AnchorPane sceneContents = new AnchorPane(background, menuBox);
            root.getChildren().addAll(sceneContents);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeOnlineGamesList() {
        if (onlineGamesDialog != null) {
            onlineGamesDialog.close();
        }
    }

    private void showOnlineGamesList() {
        BackgroundMaker.makeMenuBackgroundFrozen();
        DialogBox dialogBox = new DialogBox();
        OnlineGamesList onlineGamesList = new OnlineGamesList();
        dialogBox.getChildren().add(onlineGamesList);
        onlineGamesDialog = new DialogContainer(root, dialogBox);
        dialogBox.makeClosable(onlineGamesDialog, closeEvent -> BackgroundMaker.makeMenuBackgroundUnfrozen());

        new Thread(() -> {
            try {
                OnlineGamesListController.getInstance().requestOnlineGamesList();
                synchronized (OnlineGamesListController.getInstance()) {
                    OnlineGamesListController.getInstance().wait();
                }
                OnlineGame[] onlineGames = OnlineGamesListController.getInstance().getOnlineGames();
                Platform.runLater(() -> onlineGamesList.setItems(onlineGames));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        onlineGamesDialog.show();
    }


    private void showProfileDialog() {
        BackgroundMaker.makeMenuBackgroundFrozen();
        GridPane profileGrid = new ProfileGrid(Client.getInstance().getAccount());

        DialogBox dialogBox = new DialogBox(profileGrid);
        DialogContainer dialogContainer = new DialogContainer(root, dialogBox);

        dialogContainer.show();

        dialogBox.makeButton(logoutText, event -> {
            dialogContainer.close();
            MainMenuController.getInstance().logout();
            new LoginMenu().show();

        });
        dialogBox.makeClosable(dialogContainer, closeEvent -> BackgroundMaker.makeMenuBackgroundUnfrozen());
    }

    @Override
    public void show() {
        super.show();
        BackgroundMaker.makeMenuBackgroundUnfrozen();
        GraphicalUserInterface.getInstance().setBackgroundMusic(backgroundMusic);
    }
}
