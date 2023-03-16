import pinyin4j.PinyinHelper
import pinyin4j.format.HanyuPinyinCaseType
import pinyin4j.format.HanyuPinyinOutputFormat
import pinyin4j.format.HanyuPinyinToneType
import pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        var originalText= "我的的啊"
        val defaultFormat = HanyuPinyinOutputFormat()
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE)
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE)
        // 中文的正则表达式
        val hanziRegex = Regex("[\\u4E00-\\u9FA5]+")
        var newText =""
        try {
            for (i in 0 until originalText.length) {
                // 判断为中文,则转换为汉语拼音
                if (java.lang.String.valueOf(originalText[i]).matches(hanziRegex)) {
                    newText += PinyinHelper
                        .toHanyuPinyinStringArray(originalText[i], defaultFormat)[0]
                } else {
                    // 不为中文,则不转换
                    newText += originalText[i]
                }
            }
        } catch (e: BadHanyuPinyinOutputFormatCombination) {

        }
        System.out.println("newText:$newText")
    }
}