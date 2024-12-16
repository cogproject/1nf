import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FirstNormalForm {

    // 上限値を定数として定義
    private static final int MAX_COLUMNS = 5;         // 列数の上限
    private static final int MAX_COLON_SPLIT = 10;     // コロンで分割した後の要素数上限
    private static final int MAX_STRING_LENGTH = 10000;  // 分割後の文字列長上限

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("使用方法: java NormalizeTSVFileInput <入力TSVファイルパス>");
            System.exit(1);
        }

        String inputFilePath = args[0];

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // タブで分割
                String[] columns = line.split("\t", -1);
                
                // 列数チェック
                if (columns.length > MAX_COLUMNS) {
                    System.err.println("列数が" + MAX_COLUMNS + "を超えています: " + columns.length);
                    throw new RuntimeException("列数オーバー");
                }

                // 各列をコロン ":" で分割
                List<String[]> splitColumns = new ArrayList<>();
                boolean validLine = true;  // 行全体を有効かどうかのフラグ

                for (String col : columns) {
                    // 空文字の列を ":" split すると要素なし配列になるため、
                    // 空文字なら長さ1の配列[""]にする
                    String[] values = col.isEmpty() ? new String[]{""} : col.split(":", -1);
                    
                    // コロン分割要素数チェック
                    if (values.length > MAX_COLON_SPLIT) {
                        System.err.println("警告: セル内の値が" + MAX_COLON_SPLIT + "個を超えています。行をスキップしました。値=[" + col + "]");
                        validLine = false;
                        break;
                    }

                    // 分割後の各値の文字数チェック(0～MAX_STRING_LENGTH文字)
                    for (String val : values) {
                        if (val.length() > MAX_STRING_LENGTH) {
                            System.err.println("警告: 分割後の値が" + MAX_STRING_LENGTH + "文字を超えています。行をスキップ: [" + val + "]");
                            validLine = false;
                            break;
                        }
                        
                        if (!isPrintableAscii(val)) {
                            System.err.println("警告: 非ASCII印字可能文字が含まれています。行をスキップします。[" + val + "]");
                            validLine = false;
                            break;
                        }
                    }
                    if (!validLine) break;

                    splitColumns.add(values);
                }

                if (!validLine) {
                    // 該当行はスキップする
                    continue;
                }

                // 列ごとの部分値を直積で展開
                List<String[]> cartesianProduct = cartesianProduct(splitColumns, 0);

                // 展開した結果をTSV形式で出力
                for (String[] combination : cartesianProduct) {
                    System.out.println(String.join("\t", combination));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * splitColumnsの各列に含まれる文字列配列の直積を作成し、すべての組み合わせを返す。
     *
     * @param splitColumns 各列の値を格納した配列のリスト
     * @param index        処理中の列インデックス
     * @return 直積結果を1要素ずつ格納したリスト
     */
    private static List<String[]> cartesianProduct(List<String[]> splitColumns, int index) {
        // 再帰終了条件
        if (index == splitColumns.size()) {
            List<String[]> base = new ArrayList<>();
            base.add(new String[0]); // 要素0個の空配列をリストに入れて返す
            return base;
        }

        // 後続の列の直積を先に作り、その結果に現在の列の各値を前置きしていく
        List<String[]> partial = cartesianProduct(splitColumns, index + 1);
        List<String[]> result = new ArrayList<>();

        // 今の列が持つ複数値を繰り返し
        for (String val : splitColumns.get(index)) {
            // 後続列のすべての組み合わせに対して val を頭につける
            for (String[] arr : partial) {
                String[] newArr = new String[arr.length + 1];
                newArr[0] = val;
                System.arraycopy(arr, 0, newArr, 1, arr.length);
                result.add(newArr);
            }
        }
        return result;
    }

    /**
     * 文字列がASCII印字可能文字(0x20～0x7e)のみで構成されているか判定
     */
    private static boolean isPrintableAscii(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 0x20 || c > 0x7e) {
                return false;
            }
        }
        return true;
    }
}
