import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 플레이어의 주식 포트폴리오를 관리합니다.
 * 내부 저장소로 Map<String, Stock>을 사용하여 주식 이름으로 빠른 조회가 가능
 */
public class Portfolio {
    private final Map<String, Stock> stocks = new LinkedHashMap<>();

    /**
     * 포트폴리오에 주식을 추가하거나, 이미 존재하는 주식이라면 수량을 더하고 가격을 업데이트
     * @param stockToAdd 추가하거나 업데이트할 주식
     */
    public void addOrUpdateStock(Stock stockToAdd) {
        String name = stockToAdd.getName();
        // containsKey()로 존재 여부 확인
        if (stocks.containsKey(name)) {
            // 이미 보유 중인 주식이면 수량 합산 + 가격 업데이트
            Stock existing = stocks.get(name);
            existing.setQuantity(existing.getQuantity() + stockToAdd.getQuantity());
            existing.setPrice(stockToAdd.getPrice());
        } else {
            // 새 주식이면 맵에 추가 (key: 종목명, value: Stock 객체)
            stocks.put(name, stockToAdd);
        }
    }

    /**
     * 기존 주식의 정보(가격, 수량)를 갱신
     * 갱신 후 수량이 0 이하가 되면 포트폴리오에서 제거
     * @param stockToUpdate 갱신할 정보를 담은 주식 객체
     */
    public void updateStock(Stock stockToUpdate) {
        String name = stockToUpdate.getName();
        if (stocks.containsKey(name)) {
            Stock existing = stocks.get(name);
            existing.setPrice(stockToUpdate.getPrice());
            existing.setQuantity(stockToUpdate.getQuantity());

            // 수량이 0 이하가 되면 포트폴리오에서 제거
            if (existing.getQuantity() <= 0) {
                stocks.remove(name);
            }
        }
    }

    /**
     * 주식 이름으로 주식을 조회
     * @param name 조회할 종목명
     * @return 주식이 있으면 Optional<Stock>, 없으면 Optional.empty()
     */
    public Optional<Stock> findStockByName(String name) {
        return Optional.ofNullable(stocks.get(name));
    }

    /**
     * 포트폴리오에 있는 모든 주식 목록을 반환
     * @return Map의 values() , Stock 객체들의 컬렉션
     */
    public Collection<Stock> getAllStocks() {
        return stocks.values();
    }

    /**
     * new ArrayList<>(stocks.values())로 맵에 저장된 주식들을 인덱스 접근 가능한 List 변환
     * list.get(0) → 첫 번째 주식
     * LinkedHashMap 덕분에 삽입 순서가 유지되므로 getStocksAsList()에서 항상 일관된 순서로 번호 선택이 가능함
     */
    public List<Stock> getStocksAsList() {
        return new ArrayList<>(stocks.values());
    }
}
