
## 快速上手

```xml
<!-- maven导入 -->
<dependency>
    <groupId>io.ihankun.framework</groupId>
    <artifactId>spring-captcha-strater</artifactId>
    <version>1.2.0</version>
</dependency>
```
## 调用方式1

- 注解版风格

```java
public class Test {
    // 只需要在需要验证的controller层加入 @Captcha 注解，
    // 并且接受的参数指定成CaptchaRequest即可自动进行校验
    // 自己真实的参数可以写到 CaptchaRequest对象的泛型中
    // 如果校验失败，会抛出CaptchaValidException异常
    // 对校验失败的处理，可以使用sping的全局异常拦截CaptchaValidException异常进行处理

    @Captcha("SLIDER")
    @PostMapping("/login")
    public String login(@RequestBody CaptchaRequest<Map> request) {
        // 进入这个方法就说明已经校验成功了
        return "success";
    }
}

```

- 编码式风格(推荐使用编码式风格)

```java
public class Test2 {
    @Autowired
    private ImageCaptchaApplication application;

    public void test() {
        // 生成滑块验证码 
        CaptchaResponse<ImageCaptchaVO> res1 = application.generateCaptcha(CaptchaTypeConstant.SLIDER);


        // 匹配验证码是否正确
        // 该参数包含了滑动轨迹滑动时间等数据，用于校验滑块验证码。 由前端传入
        ImageCaptchaTrack sliderCaptchaTrack = new ImageCaptchaTrack();
        ApiResponse<?> match = application.matching(res1.getId(), sliderCaptchaTrack);
    }

}

```

### springboot配置文件说明

```yaml
# 滑块验证码配置， 详细请看 ImageCaptchaProperties 类
captcha:
  # 如果项目中使用到了redis，滑块验证码会自动把验证码数据存到redis中， 这里配置redis的key的前缀,默认是captcha:slider
  prefix: captcha
  # 验证码过期时间，默认是2分钟,单位毫秒， 可以根据自身业务进行调整
  expire:
    # 默认缓存时间 2分钟
    default: 10000
    # 针对 点选验证码 过期时间设置为 2分钟， 因为点选验证码验证比较慢，把过期时间调整大一些
    WORD_IMAGE_CLICK: 20000
  # 使用加载系统自带的资源， 默认是 false
  init-default-resource: false
  cache:
    # 缓存控制， 默认为false不开启
    enabled: true
    # 验证码会提前缓存一些生成好的验证数据， 默认是20
    cacheSize: 20
    # 缓存拉取失败后等待时间 默认是 5秒钟
    wait-time: 5000
    # 缓存检查间隔 默认是2秒钟
    period: 2000
  secondary:
    # 二次验证， 默认false 不开启
    enabled: false
    # 二次验证过期时间， 默认 2分钟
    expire: 120000
    # 二次验证缓存key前缀，默认是 captcha:secondary
    keyPrefix: "captcha:secondary"
```
### 自定义扩展
> 可以自定义 如下实现 然后直接注入到spring中即可替换默认实现,实现自定义扩展
- 生成器(`ImageCaptchaGenerator`) -- 主要负责生成滑块验证码所需的图片
- 校验器(`ImageCaptchaValidator`) -- 主要负责校验用户滑动的行为轨迹是否合规
- 资源管理器(`ImageCaptchaResourceManager`) -- 主要负责读取验证码背景图片和模板图片等
- 资源存储(`ResourceStore`) -- 负责存储背景图和模板图
- 资源提供者(`ResourceProvider`) -- 负责将资源存储器中对应的资源转换为文件流
- 滑块应用程序(`ImageCaptchaApplication`) ，上面一些接口的组合和增强，比如负责把验证的数据存到缓存中，用户一般直接使用这个接口方便的生成滑块图片和校验数据

### 默认扩展
- `SpringMultiImageCaptchaGenerator` 基于Spring的多验证码生成器
    - 基于 `MultiImageCaptchaGenerator`进行扩展
    - 可以通过手动实现`ImageCaptchaGeneratorProvider` 然后注入到spring中实现自定义验证码扩展，也可以通过该方法替换掉默认的实现

- `SecondaryVerificationApplication` 二次验证扩展
    - 基于 `ImageCaptchaApplication`进行扩展 实现了二次验证功能，
    - 该功能默认不开启
    - 可以在配置文件中配置 `captcha.secondary.endbled=true`进行手动开启
    - 使用例子

```java
public class Test3 {
    @Autowired
    private ImageCaptchaApplication sca;

    @GetMapping("/check2")
    @ResponseBody
    public boolean check2Captcha(@RequestParam("id") String id) {
        // 如果开启了二次验证
        if (sca instanceof SecondaryVerificationApplication) {
            return ((SecondaryVerificationApplication) sca).secondaryVerification(id);
        }
        return false;
    }
}

```
## 调用方式2

### 1. 使用 `ImageCaptchaGenerator`生成器生成验证码

```java
import java.util.Map;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,imageTransform).init(true);
        /*
                生成滑块验证码图片, 可选项
                SLIDER (滑块验证码)
                ROTATE (旋转验证码)
                CONCAT (滑动还原验证码)
                WORD_IMAGE_CLICK (文字点选验证码)
         */
        ImageCaptchaInfo imageCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.SLIDER);
        System.out.println(imageCaptchaInfo);

        // 负责计算一些数据存到缓存中，用于校验使用
        // ImageCaptchaValidator负责校验用户滑动滑块是否正确和生成滑块的一些校验数据; 比如滑块到凹槽的百分比值
        ImageCaptchaValidator imageCaptchaValidator = new BasicCaptchaTrackValidator();
        // 这个map数据应该存到缓存中，校验的时候需要用到该数据
        Map<String, Object> map = imageCaptchaValidator.generateImageCaptchaValidData(imageCaptchaInfo);
    }
}

```

### 2. 使用`ImageCaptchaValidator`校验器 验证

```java
public class Test2 {
    public static void main(String[] args) {
        BasicCaptchaTrackValidator sliderCaptchaValidator = new BasicCaptchaTrackValidator();

        ImageCaptchaTrack imageCaptchaTrack = null;
        Map<String, Object> map = null;
        Float percentage = null;
        // 用户传来的行为轨迹和进行校验
        // - imageCaptchaTrack为前端传来的滑动轨迹数据
        // - map 为生成验证码时缓存的map数据
        boolean check = sliderCaptchaValidator.valid(imageCaptchaTrack, map).isSuccess();
//        // 如果只想校验用户是否滑到指定凹槽即可，也可以使用
//        // - 参数1 用户传来的百分比数据
//        // - 参数2 生成滑块是真实的百分比数据
        check = sliderCaptchaValidator.checkPercentage(0.2f, percentage);
    }
}
```

## 整体架构设计

> 验证码整体分为 生成器(`ImageCaptchaGenerator`)、校验器(`ImageCaptchaValidator`)、资源管理器(`ImageCaptchaResourceManager`)
> 其中生成器、校验器、资源管理器等都是基于接口模式实现 可插拔的，可以替换为自定义实现，灵活度高

- 生成器(`ImageCaptchaGenerator`)
    - 主要负责生成行为验证码所需的图片
- 校验器(`ImageCaptchaValidator`)
    - 主要负责校验用户滑动的行为轨迹是否合规
- 资源管理器(`ImageCaptchaResourceManager`)
    - 主要负责读取验证码背景图片和模板图片等
    - 资源管理器细分为 资源存储(`ResourceStore`)、资源提供者(`ResourceProvider`)
        - 资源存储(`ResourceStore`) 负责存储背景图和模板图
        - 资源提供者(`ResourceProvider`) 负责将资源存储器中对应的资源转换为文件流
            - 一般资源存储器中存储的是图片的url地址或者id之类， 资源提供者 就是负责将url或者别的id转换为真正的图片文件
- 图片转换器 (`ImageTransform`)
    - 主要负责将图片文件流转换成字符串类型，可以是base64格式/url 或其它加密格式，默认实现是bas64格式;

## 扩展

### 生成带有混淆滑块的图片

```java
public class Test3 {
    public static void main(String[] args) {
        // 资源管理器
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        // 标准验证码生成器
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,imageTransform).init(true);
        // 生成 具有混淆的 滑块验证码 (目前只有滑块验证码支持混淆滑块， 旋转验证，滑动还原，点选验证 均不支持混淆功能)
        ImageCaptchaInfo imageCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(GenerateParam.builder()
                .type(CaptchaTypeConstant.SLIDER)
                .sliderFormatName("png")
                .backgroundFormatName("jpeg")
                // 是否添加混淆滑块
                .obfuscate(true)
                .build());
    }
}

```

### 生成webp格式的滑块图片

```java
public class Test4 {
    public static void main(String[] args) {
        // 资源管理器
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        // 标准验证码生成器
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,imageTransform).init(true);
        // 生成旋转验证码 图片类型为 webp
        ImageCaptchaInfo slideImageInfo = imageCaptchaGenerator.generateCaptchaImage(GenerateParam.builder()
                .type(CaptchaTypeConstant.ROTATE)
                .sliderFormatName("webp")
                .backgroundFormatName("webp")
                .build());
        System.out.println(slideImageInfo);
    }
}

```

### 添加自定义图片资源

- 自定义图片资源大小为 590*360 格式为jpg

```java
public class Test5 {
    public static void main(String[] args) {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        // 通过资源管理器或者资源存储器
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加classpath目录下的 aa.jpg 图片
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource(ClassPathResourceProvider.NAME, "/aa.jpg"));
        // 添加远程url图片资源
        resourceStore.addResource(CaptchaTypeConstant.SLIDER,new Resource(URLResourceProvider.NAME, "http://www.xx.com/aa.jpg"));
        // 内置了通过url 和 classpath读取图片资源，如果想扩展可实现 ResourceProvider 接口，进行自定义扩展
    }
}

```

### 添加自定义模板资源

- 模板图片格式
    - 滑块验证码
        - 滑块大小为 110*110 格式为png
        - 凹槽大小为 110*110 格式为png
    - 旋转验证码
        - 滑块大小为 200*200 格式为png
        - 凹槽大小为 200*200 格式为png
```java
public class Test6 {
    public static void main(String[] args) {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        // 通过资源管理器或者资源存储器
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        // 添加滑块验证码模板.模板图片由三张图片组成
        ResourceMap template1 = new ResourceMap("default", 4);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "/active.png"));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "/fixed.png"));
        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template1);
        // 模板与三张图片组成 滑块、凹槽、背景图
        // 同样默认支持 classpath 和 url 两种获取图片资源， 如果想扩展可实现 ResourceProvider 接口，进行自定义扩展
    }
}
```

- 清除内置的图片资源和模板资源

 ```java
public class Test6 {
    public static void main(String[] args) {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        //为方便快速上手 系统本身自带了一张图片和两套滑块模板，如果不想用系统自带的可以不让它加载系统自带的
        // 第二个构造参数设置为false时将不加载默认的图片和模板
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager, imageTransform).init(false);
    }
}

 ```

### 自定义 `imageCaptchaValidator` 校验器

```java
// 该接口负责对用户滑动验证码后传回的数据进行校验，比如滑块是否滑到指定位置，滑块行为轨迹是否正常等等
// 该接口的默认实现有 
// SimpleImageCaptchaValidator 校验用户是否滑到了指定缺口处
// BasicCaptchaTrackValidator 是对 SimpleImageCaptchaValidator增强
// BasicCaptchaTrackValidator是对SimpleImageCaptchaValidator的增强 对滑动轨迹进行了简单的验证
// 友情提示 因为BasicCaptchaTrackValidator 里面校验滑动轨迹的算法已经开源，有强制要求的建议重写该接口的方法，避免被破解
```

### 自定义 `ResourceProvider` 实现自定义文件读取策略， 比如 oss之类的

```java
import java.io.InputStream;

public class Test7 {
    public static void main(String[] args) {
        // 自定义 ResourceProvider
        ResourceProvider resourceProvider = new ResourceProvider() {
            @Override
            public InputStream getResourceInputStream(Resource data) {
                return null;
            }

            @Override
            public boolean supported(String type) {
                return false;
            }

            @Override
            public String getName() {
                return null;
            }
        };
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager,imageTransform).init(false);
        // 注册
        imageCaptchaResourceManager.registerResourceProvider(resourceProvider);
    }
}

```

### 扩展，对`StandardImageCaptchaGenerator`增加了缓存模块

> 由于实时生成滑块图片可能会有一点性能影响，内部基于`StandardSliderCaptchaGenerator`进行了提前缓存生成好的图片，`CacheSliderCaptchaGenerator` 这只是基本的缓存逻辑，比较简单，用户可以定义一些更加有意思的扩展，用于突破性能瓶颈

```java
public class Test8 {
    public static void main(String[] args) throws InterruptedException {
        // 使用 CacheSliderCaptchaGenerator 对滑块验证码进行缓存，使其提前生成滑块图片
        // 参数一: 真正实现 滑块的 SliderCaptchaGenerator
        // 参数二: 默认提前缓存多少个
        // 参数三: 出错后 等待xx时间再进行生成
        // 参数四: 检查时间间隔
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        ImageCaptchaGenerator imageCaptchaGenerator = new CacheImageCaptchaGenerator(new MultiImageCaptchaGenerator(imageCaptchaResourceManager, imageTransform), 10, 1000, 100);
        imageCaptchaGenerator.init(true);
        // 生成滑块图片
        ImageCaptchaInfo slideImageInfo = imageCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.SLIDER);
        // 获取背景图片的base64
        String backgroundImage = slideImageInfo.getBackgroundImage();
        // 获取滑块图片
        String sliderImage = slideImageInfo.getSliderImage();
        System.out.println(slideImageInfo);
    }
}
```
