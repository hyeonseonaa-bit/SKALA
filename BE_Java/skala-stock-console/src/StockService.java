/**
 * 주식 거래 관련 비즈니스 로직을 처리합니다.
 */
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * 주식 매수 처리
     * 1) 총 매수 금액 계산
     * 2) 플레이어 자금 충분 여부 확인
     * 3) 자금 차감 후 포트폴리오에 주식 추가 및 수량 업데이트
     *
     * @param player     매수할 플레이어
     * @param stockToBuy 매수할 주식 (현재 시장가 포함)
     * @param quantity   매수 수량
     * @return 처리 결과 메시지
     */
    public String buyStock(Player player, Stock stockToBuy, int quantity) {
        int totalCost = stockToBuy.getPrice() * quantity;

        // 자금 부족 확인
        if (player.getMoney() < totalCost) {
            return String.format("[매수 실패] 잔액 부족 (필요: %d원, 보유: %d원)",
                    totalCost, player.getMoney());
        }

        // 자금 차감
        player.setMoney(player.getMoney() - totalCost);

        // 포트폴리오에 주식 추가 (이미 보유 중이면 수량 합산 + 가격 업데이트)
        player.getPortfolio().addOrUpdateStock(
                new Stock(stockToBuy.getName(), stockToBuy.getPrice(), quantity));

        return String.format("[매수 성공] %s %d주 매수 (단가: %d원, 합계: %d원, 잔액: %d원)",
                stockToBuy.getName(), quantity, stockToBuy.getPrice(),
                totalCost, player.getMoney());
    }

    /**
     * 주식 매도 처리
     * 1) 포트폴리오에서 보유 주식 확인
     * 2) 보유 수량 충분 여부 확인
     * 3) 현재 시장가 기준으로 매도 금액 계산
     * 4) 자금 추가 후 포트폴리오 수량 업데이트 (0이 되면 자동 제거)
     *
     * @param player      매도할 플레이어
     * @param stockToSell 매도할 주식
     * @param quantity    매도 수량
     * @return 처리 결과 메시지
     */
    public String sellStock(Player player, Stock stockToSell, int quantity) {
        // 포트폴리오에서 보유 주식 조회
        Stock ownedStock = player.getPortfolio()
                .findStockByName(stockToSell.getName())
                .orElse(null);

        // 보유 주식 없음 확인
        if (ownedStock == null) {
            return String.format("[매도 실패] %s 미보유", stockToSell.getName());
        }

        // 보유 수량 부족 확인
        if (ownedStock.getQuantity() < quantity) {
            return String.format("[매도 실패] 수량 부족 (보유: %d주, 요청: %d주)",
                    ownedStock.getQuantity(), quantity);
        }

        // 현재 시장가 기준으로 매도 금액 계산
        Stock marketStock = stockRepository.findStock(stockToSell.getName());
        int marketPrice = (marketStock != null) ? marketStock.getPrice() : stockToSell.getPrice();
        int totalRevenue = marketPrice * quantity;

        // 자금 추가
        player.setMoney(player.getMoney() + totalRevenue);

        // 포트폴리오 수량 업데이트 (수량이 0이 되면 Portfolio 내부에서 자동 제거)
        player.getPortfolio().updateStock(
                new Stock(stockToSell.getName(), marketPrice, ownedStock.getQuantity() - quantity));

        return String.format("[매도 성공] %s %d주 매도 (시장가: %d원, 합계: %d원, 잔액: %d원)",
                stockToSell.getName(), quantity, marketPrice,
                totalRevenue, player.getMoney());
    }
}
