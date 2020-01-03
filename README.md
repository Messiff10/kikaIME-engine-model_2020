
本项目下为kika输入法引擎模型，支持emoji预测和词组预测   

## 模型说明  
- 模型结构  
该模型由两个独立的RNN模型组成：语言模型和键码模型。其中语言模型的RNN从上文单词序列中依次读入单词，不断更新自身状态并最终给出可以代表上文信息的最终状态；键码模型RNN则将该状态作为初始状态，从当前键码序列里依次读入键码，最终预测出在上文单词序列+当前键码序列的情况下，最有可能出现的几个候选词。   

- 训练  
训练分为三步，语言模型训练，词组训练和键码模型训练。若暂时不想进行词组相关的训练，可以在flags里设定use_phrase为false，自动跳过词组的训练，并且导出模型也不会导出和词组相关的参数。  
    - 语言模型训练  
      语言模型的输入与输出错位一格，根据前一个词预测后一个词。训练完语言模型后，在接下来的训练中语言模型部分的参数固定住不再变化。
    - 词组训练  
      首先要训练一个词组概率。词组概率训练，主要是训练一个二分类的softmax（其实就是逻辑回归），根据前文预测接下来词组出现的概率有多大。输入和语言模型的输入一样，最后通过语言模型RNN得到代表上文的状态输出，在该输出上接一个softmax预测接下来词组出现的概率，因此输出就是根据语料中后2-3个词是不是词组决定，即在不在词组的词表里。训完词组概率后，进行词组训练。和词组概率训练类似，区别就是二分类的softmax变为多分类的softmax来预测接下来可能是哪个词组。但有一些细节需要注意，即训练语料里如果接下来2-3个词都不在词组的词表里，那么就不会被训练，相当于补上padding；若接下来2-3个词在词组词表里，那么训练softmax时只会训练词组词表里，以该词组第一个词为开头的那些词组，即训练部分softmax。例如，I love you，需要通过I预测love you这个词组，那么训练时只会训练(love you, love me....)等以love开头的词组。训练完词组后，和词组相关的softmax参数也固定住不变。  
    - 键码模型训练  
      键码模型训练相对来说较简单，即先走一遍语言模型（语言模型参数不再更新），得到代表每个单词之前的上文的状态后，作为键码模型的初始状态训练键码模型。需要注意的是，键码模型有\<start\>标识，其功能和语言模型类似，用来预测当前还未输入任何键码时可能的候选单词或词组，因此可以省下语言模型的softmax，压缩模型体积。

- 参数设置
    - 超参数  
    效果比较稳定的超参写在static-sanity-check.cfg里，按该cfg文件的参数训练完模型后，模型大小约为20m，加上词组约为23m。量化后模型大小在4-5m左右。另外还有一些改进的空间，如把RNN隐层节点数由250改为200，学习率由0.2调高为0.5，以及dropout设为0.6，可以进一步减少模型大小，同时效果也能有所提高。
    - 键码模型mask设置  
    键码模型的mask，即权重，也是影响模型效果的重要因素。目前是设置为键码还未输全时mask均为1，输全时，若键码组成的单词和实际应该预测的单词相同，则mask为15，否则mask为5，这样可以提高正确单词的正确预测概率。另外对于emoji单词来说，是没有键码的，相当于只有一个\<start\>标识，此时mask为10，可以提高对emoji的正确预测概率。
- 推断  
    模型训好后，推断时也和训练时一样，根据已经输入的单词和键码预测最有可能出现的几个候选词。不同的是，若需要预测词组，则语言模型的候选词中需要出现词组的结果，所以会把词组的分数与词组概率相乘，得到的分数和语言模型候选词的分数一起比较，选出最高的几个单词或词组作为候选。

## 使用说明    

- 训练数据生成  
数据生成的代码在data_producer/src/main/java下
    - 执行  
      `nohup java TrainingDataProducerEmojiOutTwoUnk_file en_US unigram_path emojis_path pro_data_path wordcount_true_path letters_path output_dir lineNum wordsNum rateThreshold trainDataNum devDataNum testDataNum`  

    - 参数解释  
`${locale} 语言编码 `  
`${unigram_path} 一元大词表路径 `
`${emojis_path} emoji词表路径 `  
`${pro_data_path} 生成的字母|#|单词文件路径 `
`${wordcount_true_path} 训练所用原始语料的词频与一元大词表相比为true的文件路径 `
`${letters_path} 字母表路径 `
`${output_dir} 生成的训练数据目录，最后不包含/ ` 
`${lineNum} 用来生成训练数据原始语料的行数 `  
`${wordsNum} 用于训练的词表大小，一般为20000 `  
`${rateThreshold} 筛数据时的阈值，一般为0.8 `  
`${trainDataNum} 用于train的完整句子数 `  
`${devDataNum} 用于dev的完整句子数，一般为1万 `  
`${testDataNum} 用于test的完整句子数，一般为1万 `  

最后在指定的${data_path_out}目录下即为生成的训练数据和词表  

- 训练  
训练的代码在seq2word_split_model下
    - 执行  
      `nohup ./train.sh ${data_vocab_path} ${model_save_path} ${graph_save_path} ${config_file} ${gpu} > train.log &`  
    
    - 参数解释  
`${data_vocab_path} 上述生成的训练数据目录 `  
`${model_save_path} 模型参数保存路径 `  
`${graph_save_path} 模型保存路径，最后会保存三种模型，即后缀分别为lm、kc_full和kc_slim的模型，实际上只需要用到kc_slim模型 `  
`${config_file} 参数配置文件 `  
`${gpu} 指定GPU` 

- 测试  
    - 执行      
    `nohup python test.py ${graph_file} ${data_vocab_path} ${full_vocab_file} ${config_file} ${test_file} ${gpu} > test.log &`  
    
    - 参数解释  
`${graph_file} 模型文件，即保存的kc_slim模型 `    
`${data_vocab_path} 词表路径 `  
`${full_vocab_file} 16万大词表文件 `  
`${config_file} 参数配置文件 `  
`${test_file} 测试文件 `  
`${gpu} 指定GPU` 

测试结果将输出在"test_result"文件中,使用input-efficiency-analysis/test的EfficiencyAnalyzerNewFormTest进行测试，可以得到最终输入效率、准确率等指标。



## Build Dictionary

```
$ cd dict_maker
```

### Prerequisities

1. jdk
2. python3
3. gradle
4. bazel-0.11.1
5. tensorflow==1.4.1

### Build
``` shell
$ bash -e build.sh
```

### Run

``` shell
$ bash -e run.sh <locale> <src_dir> <frozen_graph_file>
```
