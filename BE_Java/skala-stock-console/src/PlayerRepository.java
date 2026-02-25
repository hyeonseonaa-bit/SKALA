import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 플레이어 데이터를 파일에 저장하고 메모리에 로드
 * 내부 저장소로 Map<String, Player>를 사용하여 플레이어 ID로 빠르게 검색
 * LinkedHashMap으로 플레이어 추가 순서 유지
 */
public class PlayerRepository {
    private static final String PLAYER_FILE = "players.txt";
    private final Map<String, Player> playerMap = new LinkedHashMap<>();
    private final PlayerMapper mapper = new PlayerMapper();

    /**
     * 파일의 모든 라인을 읽어 PlayerMapper로 Player 객체 변환 후 맵에 저장
     * 파일이 없으면 조용히 종료
     */
    public void loadPlayerList() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PLAYER_FILE))) {
            String line;
            // BufferedReader로 파일을 한 줄씩 읽어 mapper.fromLine()으로 변환 후 맵에 put 
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    Player player = mapper.fromLine(line);
                    playerMap.put(player.getId(), player);
                }
            }
        } catch (IOException e) {
            // 파일이 없으면 빈 맵으로 시작 (최초 실행 시 정상)
            System.out.println("저장된 플레이어 데이터가 없습니다. 새로 시작합니다.");
        }
    }

    /**
     * 맵의 모든 Player 객체를 PlayerMapper로 문자열 변환 후 파일에 저장
     * 파일이 없으면 새로 생성
     */
    public void savePlayerList() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PLAYER_FILE))) {
            // 맵의 모든 플레이어를 한 줄씩 파일에 기록
            for (Player player : playerMap.values()) {
                writer.write(mapper.toLine(player));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("플레이어 데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 플레이어 ID로 맵에서 Player 객체를 검색
     * @return 찾은 Player 객체, 없으면 null
     */
    public Player findPlayer(String id) {
        return playerMap.get(id);
    }

    /**
     * 새 플레이어를 맵에 추가
     * @param player 추가할 Player 객체
     */
    public void addPlayer(Player player) {
        playerMap.put(player.getId(), player);
    }
}
