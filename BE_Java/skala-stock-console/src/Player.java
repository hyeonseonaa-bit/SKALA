/**
 * 플레이어의 기본 정보(ID, 자금)와 포트폴리오를 관리합니다.
 */
public class Player {
    private String id;
    private int money; 
    // final: 한 번 할당되면 재할당 불가, 포트폴리오 객체 자체는 교체할 수 없지만 포트폴리오 내부의 데이터(주식 목록 등)는 변경 가능
    private final Portfolio portfolio;

    /**
     * 플레이어 생성자
     * @param id           플레이어 ID
     * @param initialMoney 초기 보유 자금
     * portfolio는 생성자 내부에서 new Portfolio()로 자동 생성됨 (외부에서 주입 할 필요가 없다)
     */
    public Player(String id, int initialMoney) {
        this.id = id;
        this.money = initialMoney;
        this.portfolio = new Portfolio(); // 플레이어 생성 시 빈 포트폴리오 생성
    }

    // Getters
    // portfolio는 final이므로 getter만 제공
    public String getId() { return id; }
    public int getMoney() { return money; }
    public Portfolio getPortfolio() { return portfolio; }

    // Setters (portfolio는 final이므로 setter 없음)
    // 주식 매수/매도 시 자금 증감에 사용
    // 주식 매수 시 player.setMoney(player.getMoney() - 구매금액) 형태로 사용됨.
    public void setId(String id) { this.id = id; }
    public void setMoney(int money) { this.money = money; }
}
