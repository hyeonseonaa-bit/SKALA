/**
 * 프로그램의 진입점이자 컨트롤러 역할을 합니다.
 *   App.main()                                                                                                                           
    └─ SkalaStockMarket (컨트롤러)
         ├─ StockView        → 입출력만 담당                                                                                           
         ├─ StockRepository  → 시장 주식 데이터                                                                                      
         ├─ PlayerRepository → 플레이어 파일 저장/로드
         └─ StockService     → 매수/매도 비즈니스 로직
 */
public class SkalaStockMarket {

    // 각 역할별 컴포넌트 (final: 한 번 생성 후 교체 불가)
    private final PlayerRepository playerRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;  
    private final StockView stockView;

    // 현재 로그인한 플레이어 (게임 중 계속 유지)
    private Player player;

    /**
     * 생성자: 모든 의존 객체를 직접 생성
     * StockService는 StockRepository에 의존하므로 먼저 생성 후 주입
     */
    public SkalaStockMarket() {
        playerRepository = new PlayerRepository();
        stockRepository = new StockRepository();
        stockService = new StockService(stockRepository); // 의존성 주입
        stockView = new StockView();
    }

    /**
     * 프로그램 전체 생명주기를 관리
     */
    public void start() {
        // 1. 파일에서 주식/플레이어 데이터 메모리에 로드
        stockRepository.loadStockList();
        playerRepository.loadPlayerList();

        // 2. 플레이어 로그인 또는 신규 생성
        initializePlayer();

        // 3. 현재 플레이어 정보 출력
        stockView.displayPlayerInfo(player);

        // 4. 0번 선택 전까지 메뉴 반복
        mainLoop();

        // 5. 종료 시 변경된 플레이어 데이터를 파일에 저장
        playerRepository.savePlayerList();
        stockView.showMessage("프로그램을 종료합니다...Bye");
        stockView.close();
    }

    // 플레이어 초기화
    private void initializePlayer() {
        String playerId = stockView.promptForPlayerId();

        // 기존 플레이어 조회 (null이면 신규)
        player = playerRepository.findPlayer(playerId);
        if (player == null) {
            int money = stockView.promptForInitialMoney();
            player = new Player(playerId, money);
            playerRepository.addPlayer(player);
        }
    }

    
    // 메인 루프: 0번(종료)을 선택할 때까지 메뉴를 반복 출력하고 입력을 처리
    // switch문으로 각 메뉴 번호에 맞는 기능을 분기
    private void mainLoop() {
        boolean running = true;
        while (running) {
            int code = stockView.showMenuAndGetSelection();
            switch (code) {
                case 1 -> // 나의 자산 확인
                    stockView.displayPlayerInfo(player);
                case 2 -> // 주식 구매
                    buyStock();
                case 3 -> // 주식 판매
                    sellStock();
                case 0 -> // 종료
                    running = false;
                default -> stockView.showMessage("올바른 번호를 선택하세요.");
            }
        }
    }

    //주식 매수 프로세스
    private void buyStock() {
        // 현재 시장 주식 목록 출력 (번호 포함)
        stockView.displayStockList(stockRepository.getAllStocks());

        // 사용자가 선택한 번호 → 0-based index로 변환되어 반환됨
        int index = stockView.getStockIndexFromUser();
        Stock stock = stockRepository.findStock(index);
        if (stock == null) {
            stockView.showMessage("유효하지 않은 종목 번호입니다.");
            return;
        }

        int quantity = stockView.getQuantityFromUser();

        // 비즈니스 로직은 StockService에 위임, 결과는 문자열로 반환
        String result = stockService.buyStock(player, stock, quantity);
        stockView.showMessage(result);
    }

    //주식 매도 프로세스
    private void sellStock() {
        // 보유 주식 없으면 바로 종료
        if (player.getPortfolio().getAllStocks().isEmpty()) {
            stockView.showMessage("보유 중인 주식이 없습니다.");
            return;
        }

        // 포트폴리오 출력 후 종목 번호 선택
        stockView.displayPlayerInfo(player);
        int index = stockView.getStockIndexFromUser();

        // 포트폴리오 리스트에서 선택한 인덱스의 주식 조회
        // stream().skip(index).findFirst(): 포트폴리오 리스트에서 특정 인덱스의 주식을 꺼내는 스트림 방식
        Stock stock = player.getPortfolio().getStocksAsList()
                .stream()
                .skip(index)
                .findFirst()
                .orElse(null);
        if (stock == null) {
            stockView.showMessage("유효하지 않은 종목 번호입니다.");
            return;
        }

        int quantity = stockView.getQuantityFromUser();

        // 매도는 현재 시장가 기준으로 처리됨 (StockService 내부에서 시장가 조회)
        String result = stockService.sellStock(player, stock, quantity);
        stockView.showMessage(result);
    }
}
