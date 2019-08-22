rsa和aes使用的方法如下:
#第一步
导入公钥pem，获取公钥后用来加密aes
#第二步
随机生成一个AES密钥，拿到Aes密钥后加密要传输的内容
#第三步
将被加密的内容，以及AES密钥和RSA公钥进行传输到服务器解密内容
#-----------------------------------------------下面是具体的代码实现-----------------------------------------------
//获取Rsa公钥，用来加密Aes的key，AES用来加密传输的内容
        RSAPublicKey publicKey = null;
        String encryptAESkey = null;
        String encryptRSAKey = null;
        try {
            publicKey = Tools.RSAUtils.loadPublicKey(App.getAppCtx().getAssets().open("rsa_public_key.pem"));
            //生成一个密钥随机的
            encryptAESkey = Tools.AESUtil.generateKeyString();
            encryptRSAKey = Tools.RSAUtils.encryptByPublicKey(encryptAESkey, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取Gson的实例
        final GsonProvider gsonProvider = GsonProvider.getInstance();
        final Gson gson = gsonProvider.getGson();
        final String encryptInfo = gson.toJson(t);
        final String encryptContent = Tools.AESUtil.encrypt(encryptInfo, encryptAESkey);
        final UpEncryptInfo info = new UpEncryptInfo(encryptContent, encryptRSAKey,encryptAESkey);
        
         //目标项是否在最后一个可见项之后
            private boolean mShouldScroll;
            //记录目标项位置
            private int mToPosition;
        
            /**
             * 滑动到指定位置
             */
            private void smoothMoveToPosition(final int position) {
                // 第一个可见位置
                int firstItem = mRv.getChildLayoutPosition(mRv.getChildAt(0));
                // 最后一个可见位置
                int lastItem = mRv.getChildLayoutPosition(mRv.getChildAt(mRv.getChildCount() - 1));
                if (position < firstItem) {
                    // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
                    mRv.smoothScrollToPosition(position);
                } else if (position <= lastItem) {
                    // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
                    int movePosition = position - firstItem;
                    if (movePosition >= 0 && movePosition < mRv.getChildCount()) {
                        int top = mRv.getChildAt(movePosition).getTop();
                        // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                        mRv.smoothScrollBy(0, top);
                    }
                } else {
                    // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
                    // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
                    mRv.smoothScrollToPosition(position);
                    mToPosition = position;
                    mShouldScroll = true;
                }
            }