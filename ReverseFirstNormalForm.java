import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/*
実行方法:リダイレクトで実行

java ReverseFirstNormalForm < 入力TSVファイル

*/

public class ReverseFirstNormalForm {
    // 行の最大数(上限)
    private static final int MAX_LINES = 1000;
    // セル内の文字数上限
    private static final int MAX_CELL_LENGTH = 100;
    // 1つのキーに紐づく値の最大個数
    private static final int MAX_GROUP_VALUES = 10;

    public static void main(String[] args) {
        Map<String, List<String>> map = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                lineCount++;
                // 行の最大数を超えた場合はスキップ or break
                if (lineCount > MAX_LINES) {
                    System.err.println("警告: 行数が1000を超えました。この行以降は処理しません。");
                    break;
                }

                // タブ区切りで分割
                String[] cols = line.split("\t", -1);

                // 列数2以外はスキップ
                if (cols.length != 2) {
                    System.err.println("警告: 列数が2列ではないためスキップしました: " + line);
                    continue;
                }

                // 1列目 (キー) と 2列目 (値)
                String key = cols[0];
                String value = cols[1];

                // それぞれのセルに対して文字数・ASCII範囲をチェック
                if (!isValidCell(key) || !isValidCell(value)) {
                    System.err.println("警告: セルの要件を満たさない(ASCII印字可能文字以外 or 長さ超過)行をスキップ: " + line);
                    continue;
                }

                // mapにキーが無ければ初期化
                map.putIfAbsent(key, new ArrayList<>());
                List<String> values = map.get(key);

                // グループ化される値がすでに10個なら、これ以上追加しないかエラーとする
                if (values.size() >= MAX_GROUP_VALUES) {
                    System.err.println("警告: キー[" + key + "]の値が10個を超えるため、追加をスキップしました。値=[" + value + "]");
                    continue;
                }

                values.add(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // グループ化した結果をコロン(:)で連結して出力
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            // コロンで連結
            String joined = String.join(":", values);
            System.out.println(key + "\t" + joined);
        }
    }

    /**
     * セルが以下の要件を満たすか判定する:
     * - 文字数が0文字以上100文字以下
     * - すべてASCII印字可能文字(0x20～0x7e)
     */
    private static boolean isValidCell(String cell) {
        if (cell.length() > MAX_CELL_LENGTH) {
            return false;
        }
        for (int i = 0; i < cell.length(); i++) {
            char c = cell.charAt(i);
            if (c < 0x20 || c > 0x7e) {
                return false;
            }
        }
        return true;
    }
}
