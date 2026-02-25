import java.util.List;

/**
 * Player 객체와 파일 데이터(문자열)를 상호 변환합니다.
 */
public class PlayerMapper {

    //"id,money,stockName1:price1:qty1|stockName2:price2:qty2" 형식의 문자열을 Player 객체로 변환
     
    public Player fromLine(String line) {
        // 콤마로 문자열을 분리 → [id, money, 주식목록]
        String[] parts = line.split(",", 3);
        
        String id = parts[0];
        int money = Integer.parseInt(parts[1]);

        Player player = new Player(id, money);

        // 주식 목록이 있을 때만 파싱
        if (parts.length == 3 && !parts[2].isEmpty()) {
            // 파이프(|)로 개별 주식 문자열 분리
            String[] stockTokens = parts[2].split("\\|");
            for (String token : stockTokens) {
                // 콜론(:)으로 name, price, quantity 분리
                String[] stockData = token.split(":");
                String name  = stockData[0];
                int price    = Integer.parseInt(stockData[1]);
                int quantity = Integer.parseInt(stockData[2]);
                player.getPortfolio().addOrUpdateStock(new Stock(name, price, quantity));
            }
        }

        return player;
    }

    // Player 객체를 "id,money,stockName1:price1:qty1|stockName2:price2:qty2" 형식의 문자열로 변환

    public String toLine(Player player) {
        StringBuilder sb = new StringBuilder();

        // id와 money를 콤마로 연결
        sb.append(player.getId()).append(",").append(player.getMoney());

        List<Stock> stocks = player.getPortfolio().getStocksAsList();
        if (!stocks.isEmpty()) {
            sb.append(",");
            // 각 주식을 "name:price:qty" 형식으로 만들고 파이프(|)로 연결
            StringBuilder stockSb = new StringBuilder();
            for (Stock stock : stocks) {
                if (stockSb.length() > 0) stockSb.append("|");
                stockSb.append(stock.getName())
                       .append(":").append(stock.getPrice())
                       .append(":").append(stock.getQuantity());
            }
            sb.append(stockSb);
        }

        return sb.toString();
    }
}
