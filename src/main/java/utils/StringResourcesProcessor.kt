package utils

import com.intellij.openapi.vfs.VirtualFile
import pinyin4j.PinyinHelper
import pinyin4j.format.HanyuPinyinCaseType
import pinyin4j.format.HanyuPinyinOutputFormat
import pinyin4j.format.HanyuPinyinToneType
import pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination
import ui.HardcodedStringEntryModel
import utils.FileProcessor.readFileContent


/*  This class will be able to process all types of file i.e. kotlin, java , xml files and replace all hard-coded strings
*/

object StringResourcesProcessor {

    fun process(
        hardcodedStrings: List<String>,
        virtualFile: VirtualFile,
        stringXMLFile: VirtualFile
    ): List<HardcodedStringEntryModel> {
        val keysToAddInStringXML = mutableListOf<String>()
        val prefix = LocalPersistenceUtils.getData(Constants.PREFERENCES.PREFIX) ?: "str_"
        val entries = mutableListOf<HardcodedStringEntryModel>()
        if (virtualFile.name.endsWith(".xml")) {
            for (stringResourceValue in hardcodedStrings) {
                val stringXMLContent = readFileContent(stringXMLFile)
                val stringResourceKey =
                    getKeyForStringResource(stringXMLContent, prefix + stringResourceValue, 0, keysToAddInStringXML)
                keysToAddInStringXML.add(stringResourceKey)
                entries.add(HardcodedStringEntryModel(stringResourceKey, stringResourceValue, false, "", virtualFile))
            }
        } else if (virtualFile.path.contains("/main/java")) {
            val extractTemplate = Constants.javaExtractTemplate
            for (stringResourceValue in hardcodedStrings) {
                val stringXMLContent = readFileContent(stringXMLFile)
                val stringResourceKey =
                    getKeyForStringResource(stringXMLContent, prefix + stringResourceValue, 0, keysToAddInStringXML)
                keysToAddInStringXML.add(stringResourceKey)
                entries.add(
                    HardcodedStringEntryModel(
                        stringResourceKey,
                        stringResourceValue,
                        false,
                        extractTemplate,
                        virtualFile
                    )
                )
            }
        }
        return entries
    }

    private fun getKeyForStringResource(
        stringsXMLFileContent: String,
        originalText: String,
        repeatCount: Int,
        stringsToAddInStringXMLFile: MutableList<String>
    ): String {
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
        val transformedText = Constants.REGEX.KEY_GENERATOR_REGEX.replace(newText, "").replace(" ", "_")
        if (stringsXMLFileContent.contains(transformedText) || stringsToAddInStringXMLFile.contains(transformedText)) {
            return getKeyForStringResource(
                stringsXMLFileContent,
                transformedText.plus("_" + repeatCount + 1),
                repeatCount + 1,
                stringsToAddInStringXMLFile
            )
        }
        return transformedText
    }


}