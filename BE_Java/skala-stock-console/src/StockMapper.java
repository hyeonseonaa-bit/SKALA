/**
 * Stock 객체와 파일 데이터(문자열)를 상호 변환합니다.
 * 저장 형식: "주식명,가격" (수량은 저장하지 않음)
 */
public class StockMapper {

    /**
     * "주식명,가격" 형식의 문자열을 파싱해서 Stock 객체를 생성하고 반환
     * 수량은 파일에 저장되지 않으므로 0으로 초기화
     * @param line 파일에서 읽어온 한 줄 문자열 
     * @return 파싱된 Stock 객체 (quantity = 0)
     * 플레이어별 보유 수량은 PlayerMapper가 따로 관리하기 때문에 수량을 0으로 초기화
     */
    public Stock fromLine(String line) {
        // 콤마(,)로 분리 → [주식명, 가격]
        String[] parts = line.split(",");
        String name = parts[0];
        int price   = Integer.parseInt(parts[1]);
        return new Stock(name, price, 0); 
    }

    /**
     * toLine(Stock)으로 Stock 객체를 "주식명,가격" 형식의 문자열로 변환
     * 수량은 시장 데이터에 해당하지 않으므로 저장하지 않음
     * @param stock 변환할 Stock 객체
     * @return 파일에 저장할 한 줄 문자열
     */
    public String toLine(Stock stock) {
        return stock.getName() + "," + stock.getPrice();
    }
}
