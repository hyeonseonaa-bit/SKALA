import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 주식 데이터를 파일에 저장하고 메모리에 로드
 * 내부 저장소로 List<Stock>을 사용
 */
public class StockRepository {
    private static final String STOCK_FILE = "stocks.txt";
    private final List<Stock> stockList = new ArrayList<>();
    private final StockMapper mapper = new StockMapper();

    /**
     * loadStockList()메서드로 파일을 한 줄씩 읽어 mapper.fromLine()으로 변환 후 리스트에 추가
     * 파일이 없으면 initializeDefaultStocks() 호출하여 기본 주식 목록 생성
     */
    public void loadStockList() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STOCK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    stockList.add(mapper.fromLine(line));
                }
            }
        } catch (IOException e) {
            // 파일이 없으면 기본 주식 목록으로 초기화
            System.out.println("주식 데이터 파일이 없어 기본 목록으로 초기화합니다.");
            initializeDefaultStocks();
        }
    }

    /**
     * 기본 주식 목록을 리스트에 추가
     * loadStockList()에서 파일이 없을 때 호출
     */
    private void initializeDefaultStocks() {
        stockList.add(new Stock("TechCorp", 152, 0));
        stockList.add(new Stock("GreenEnergy", 88, 0));
        stockList.add(new Stock("HealthPlus", 210, 0));
        stockList.add(new Stock("BioGen", 75, 0));
    }

    /**
     * getAllStocks()메서드로 전체 주식 목록을 반환
     * new ArrayList<>(stockList)로 원본 리스트를 보호하고 복사본을 반환
     */
    public List<Stock> getAllStocks() {
        return new ArrayList<>(stockList);
    }

    /**
     * findStock(int index)메서드로 인덱스로 0부터 주식 검색
     * 범위 체크 후 stockList.get(index) 반환, 범위 초과 시 null
     * @param index 0-based 인덱스
     * @return 해당 주식, 범위를 벗어나면 null
     */
    public Stock findStock(int index) {
        if (index < 0 || index >= stockList.size()) {
            return null;
        }
        return stockList.get(index);
    }

    /**
     * findStock(String name)메서드로 리스트를 순회하면 이름이 일치하는 항목을 반환
     * @param name 검색할 종목명
     * @return 해당 주식, 없으면 null
     */
    public Stock findStock(String name) {
        for (Stock stock : stockList) {
            if (stock.getName().equals(name)) {
                return stock;
            }
        }
        return null;
    }
}
