package sample.model;

import sample.controller.Controller;

import java.util.HashMap;
import java.util.Map;

public class Model {
    private long counter;
    public Controller controller;
    public CardModel currCard;
    private FileMenu fm;
    private EditMenu em;
    private HelpMenu hm;
    private ViewMenu vm;
    private Map<Long, CardModel> cards;
    public Model(){
        counter = 0;
        cards = new HashMap<>();
    }

    public void setController(Controller controller){
        this.controller = controller;
        em = new EditMenu(controller, this);
        fm = new FileMenu(controller, this);
        hm = new HelpMenu(controller, this);
        vm = new ViewMenu(controller, this);
        createNewCard();
    }
    public void createNewCard(){
        CardModel card = new CardModel(counter);
        currCard = card;
        fm.createCard(card);
        for (Map.Entry<Long, CardModel> entry : cards.entrySet())
            if(cards.get(entry.getKey()).getId()!=counter) {
                cards.get(entry.getKey()).setNowI(false);
            }
        cards.put(counter, card);
        controller.modelCreateCard(counter++);
    }

    public void setCurrentTab(){
        for (Map.Entry<Long, CardModel> entry : cards.entrySet()) {
            if(entry.getValue().isNowI()){
                currCard = entry.getValue();
                return;
            }
        }
    }

    public CardModel getCard(long id){
        return cards.get(id);
    }

    public FileMenu getFileMenu() {
        return fm;
    }

    public EditMenu getEditMenu() {
        return em;
    }

    public HelpMenu getHelpMenu() {
        return hm;
    }

    public ViewMenu getViewMenu() {
        return vm;
    }

}
