コンパイル  
各Javaファイルをコンパイルします。  

javac *.java  

実行  
1. 第一正規化
java FirstNormalForm <入力TSVファイルパス>

<実行結果>　　
% java FirstNormalForm in1.tsv  
apple	fruit
apple	sale
banana	fruit
cherry	fruit
	beverage


2. 第一正規化の逆変換
java ReverseFirstNormalForm < <入力TSVファイルパス>

<実行結果>  
% java ReverseFirstNormalForm < in2.tsv
beverage	:coke
fruit	apple:banana:banana
pet	dog
