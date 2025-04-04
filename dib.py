import torch
import torch.nn as nn

# ---------------------------
# 🟢 1. Stem Block
# ---------------------------
class StemBlock(nn.Module):
    def __init__(self, in_channels=3, out_channels=64):
        super(StemBlock, self).__init__()
        self.conv = nn.Conv2d(in_channels, out_channels, kernel_size=3, padding=1, stride=1)
        self.bn = nn.BatchNorm2d(out_channels)
        self.relu = nn.ReLU()
        self.pool = nn.MaxPool2d(kernel_size=2, stride=2)

    def forward(self, x):
        x = self.conv(x)  # Output: (64, H, W)
        x = self.bn(x)
        x = self.relu(x)
        x = self.pool(x)
        return x

# ---------------------------
# 🔵 2. SCRDA Block (Same as before)
# ---------------------------

# class SCRDA_Block(nn.Module):
#     def __init__(self, in_channels, filters1):
#         super(SCRDA_Block, self).__init__()

#         # First Branch (Left Path)
#         self.maxpool = nn.MaxPool2d(kernel_size=3, stride=1, padding=1)

#         # Second Branch (Right Path)
#         self.sep_conv1x1_1 = nn.Conv2d(in_channels, filters1, kernel_size=1)  # Separable 1x1
#         self.bn1x1_1 = nn.BatchNorm2d(filters1)
#         self.relu1x1_1 = nn.ReLU()

#         # 3x3 Depthwise + Pointwise Conv
#         self.sep_conv3x3_1 = nn.Conv2d(filters1, filters1, kernel_size=3, padding=1, groups=filters1)
#         self.pointwise1 = nn.Conv2d(filters1, filters1, kernel_size=1)
#         self.bn3x3_1 = nn.BatchNorm2d(filters1)
#         self.relu3x3_1 = nn.ReLU()

#         # Another 3x3 Depthwise + Pointwise Conv
#         self.sep_conv3x3_2 = nn.Conv2d(filters1, filters1, kernel_size=3, padding=1, groups=filters1)
#         self.pointwise2 = nn.Conv2d(filters1, filters1, kernel_size=1)
#         self.bn3x3_2 = nn.BatchNorm2d(filters1)
#         self.relu3x3_2 = nn.ReLU()

#         # Separate 1x1 Path
#         self.sep_conv1x1_2 = nn.Conv2d(in_channels, filters1, kernel_size=1)  # Second 1x1 branch
#         self.bn1x1_2 = nn.BatchNorm2d(filters1)
#         self.relu1x1_2 = nn.ReLU()

#         # Concatenation Layer
#         self.bn_concat = nn.BatchNorm2d(filters1 * 2)
#         self.relu_concat = nn.ReLU()

#     def forward(self, x):
#         # Left Path
#         x1 = self.maxpool(x)

#         # Right Path
#         x2 = self.sep_conv1x1_1(x)
#         x2 = self.bn1x1_1(x2)
#         x2 = self.relu1x1_1(x2)

#         # Separate 1x1 Path
#         x3 = self.sep_conv1x1_2(x)
#         x3 = self.bn1x1_2(x3)
#         x3 = self.relu1x1_2(x3)

#         x3 = self.sep_conv3x3_1(x3)
#         x3 = self.pointwise1(x3)
#         x3 = self.bn3x3_1(x3)
#         x3 = self.relu3x3_1(x3)

#         x3 = self.sep_conv3x3_2(x3)
#         x3 = self.pointwise2(x3)
#         x3 = self.bn3x3_2(x3)
#         x3 = self.relu3x3_2(x3)

#         # Concatenation
#         x_concat = torch.cat([x1, x2, x3], dim=1)
#         x_concat = self.bn_concat(x_concat)
#         x_concat = self.relu_concat(x_concat)

#         return x_concat  # Output Shape: (3 * filters1, H, W)

class SCRDA_Block(nn.Module):
    def __init__(self, in_channels, filters1, filters2):  # Added filters2
        super(SCRDA_Block, self).__init__()

        # First Branch (Left Path)
        self.maxpool = nn.MaxPool2d(kernel_size=3, stride=1, padding=1)

        # Second Branch (Right Path)
        self.sep_conv1x1_1 = nn.Conv2d(in_channels, filters1, kernel_size=1)  # Separable 1x1
        self.bn1x1_1 = nn.BatchNorm2d(filters1)
        self.relu1x1_1 = nn.ReLU()

        # 3x3 Depthwise + Pointwise Conv
        self.sep_conv3x3_1 = nn.Conv2d(filters1, filters2, kernel_size=3, padding=1, groups=filters1)
        self.pointwise1 = nn.Conv2d(filters2, filters2, kernel_size=1)
        self.bn3x3_1 = nn.BatchNorm2d(filters2)
        self.relu3x3_1 = nn.ReLU()

        # Another 3x3 Depthwise + Pointwise Conv
        self.sep_conv3x3_2 = nn.Conv2d(filters2, filters2, kernel_size=3, padding=1, groups=filters2)
        self.pointwise2 = nn.Conv2d(filters2, filters2, kernel_size=1)
        self.bn3x3_2 = nn.BatchNorm2d(filters2)
        self.relu3x3_2 = nn.ReLU()

        # Separate 1x1 Path
        self.sep_conv1x1_2 = nn.Conv2d(in_channels, filters1, kernel_size=1)  # Second 1x1 branch
        self.bn1x1_2 = nn.BatchNorm2d(filters1)
        self.relu1x1_2 = nn.ReLU()

        # Concatenation Layer
        self.bn_concat = nn.BatchNorm2d(in_channels + filters1 + filters2)  # Updated for correct channel count
        self.relu_concat = nn.ReLU()

    def forward(self, x):
        # Left Path
        x1 = self.maxpool(x)

        # Right Path
        x2 = self.sep_conv1x1_1(x)
        x2 = self.bn1x1_1(x2)
        x2 = self.relu1x1_1(x2)

        # Separate 1x1 Path
        x3 = self.sep_conv1x1_2(x)
        x3 = self.bn1x1_2(x3)
        x3 = self.relu1x1_2(x3)

        x3 = self.sep_conv3x3_1(x3)
        x3 = self.pointwise1(x3)
        x3 = self.bn3x3_1(x3)
        x3 = self.relu3x3_1(x3)

        x3 = self.sep_conv3x3_2(x3)
        x3 = self.pointwise2(x3)
        x3 = self.bn3x3_2(x3)
        x3 = self.relu3x3_2(x3)

        # Concatenation
        x_concat = torch.cat([x1, x2, x3], dim=1)
        x_concat = self.bn_concat(x_concat)
        x_concat = self.relu_concat(x_concat)

        return x_concat  # Output Shape: (in_channels + filters1 + filters2, H, W)

# ---------------------------
# 🔴 3. SCRDB Block (New Addition)
# ---------------------------
class SCRDB_Block(nn.Module):
    def __init__(self, in_channels, filters1, filters2):
        super(SCRDB_Block, self).__init__()

        # First Branch (Left Path)
        self.maxpool = nn.MaxPool2d(kernel_size=3, stride=1, padding=1)

        # Second Branch (Right Path)
        self.sep_conv1x1_1 = nn.Conv2d(in_channels, filters1, kernel_size=1)  # Separable 1x1
        self.bn1x1_1 = nn.BatchNorm2d(filters1)
        self.relu1x1_1 = nn.ReLU()

        # 3x3 Depthwise + Pointwise Conv
        self.sep_conv3x3_1 = nn.Conv2d(filters1, filters2, kernel_size=3, padding=1, groups=filters1)
        self.pointwise1 = nn.Conv2d(filters2, filters2, kernel_size=1)
        self.bn3x3_1 = nn.BatchNorm2d(filters2)
        self.relu3x3_1 = nn.ReLU()

        # Another 3x3 Depthwise + Pointwise Conv
        self.sep_conv3x3_2 = nn.Conv2d(filters2, filters2, kernel_size=3, padding=1, groups=filters1)
        self.pointwise2 = nn.Conv2d(filters2, filters2, kernel_size=1)
        self.bn3x3_2 = nn.BatchNorm2d(filters2)
        self.relu3x3_2 = nn.ReLU()

        # Separate 1x1 Path
        self.sep_conv1x1_2 = nn.Conv2d(filters1, filters2, kernel_size=1)  # Second 1x1 branch
        self.bn1x1_2 = nn.BatchNorm2d(filters2)
        self.relu1x1_2 = nn.ReLU()

        # Concatenation Layer
        self.bn_concat = nn.BatchNorm2d(in_channels+3*filters2)
        self.relu_concat = nn.ReLU()

    def forward(self, x):
        # Left Path
        x1 = self.maxpool(x)

        # Total Right Path
        x2 = self.sep_conv1x1_1(x)
        x2 = self.bn1x1_1(x2)
        x2 = self.relu1x1_1(x2)

        x2 = self.sep_conv3x3_1(x2)
        x2 = self.pointwise1(x2)
        x2 = self.bn3x3_1(x2)
        x2 = self.relu3x3_1(x2)
    
        # 2nd path Path
        x4 = self.sep_conv1x1_1(x)
        x4 = self.bn1x1_1(x4)
        x4 = self.relu1x1_1(x4)

        x4 = self.sep_conv1x1_2(x4)
        x4 = self.bn1x1_2(x4)
        x4 = self.relu1x1_2(x4)


        # Separate 1x1 Path
        x3 = self.sep_conv1x1_1(x)
        x3 = self.bn1x1_1(x3)
        x3 = self.relu1x1_1(x3)

        x3 = self.sep_conv3x3_1(x3)
        x3 = self.pointwise1(x3)
        x3 = self.bn3x3_1(x3)
        x3 = self.relu3x3_1(x3)

        x3 = self.sep_conv3x3_2(x3)
        x3 = self.pointwise2(x3)
        x3 = self.bn3x3_2(x3)
        x3 = self.relu3x3_2(x3)

        # Concatenation
        x_concat = torch.cat([x1, x2, x3, x4], dim=1)
        x_concat = self.bn_concat(x_concat)
        x_concat = self.relu_concat(x_concat)

        return x_concat  # Output Shape: (4 * filters1, H, W)

# ---------------------------
# 🟠 4. Transition Layer
# ---------------------------

class TransitionLayer(nn.Module):
    def __init__(self, in_channels, mid_channels, out_channels):
        super(TransitionLayer, self).__init__()

        # 2x2 Convolution + BatchNorm + ReLU
        self.conv2x2 = nn.Conv2d(in_channels, mid_channels, kernel_size=2, stride=1, padding=1)
        self.bn1 = nn.BatchNorm2d(mid_channels)
        self.relu1 = nn.ReLU()

        # Upsampling 2x2
        self.upsample = nn.Upsample(scale_factor=2, mode='nearest')

        # Depthwise Convolution 1x1 + BatchNorm + ReLU
        self.depthwise = nn.Conv2d(mid_channels, mid_channels, kernel_size=1, groups=mid_channels)
        self.bn2 = nn.BatchNorm2d(mid_channels)
        self.relu2 = nn.ReLU()

        # Separable Convolution 1x1 + BatchNorm + ReLU
        self.pointwise = nn.Conv2d(mid_channels, mid_channels, kernel_size=1)
        self.bn3 = nn.BatchNorm2d(mid_channels)
        self.relu3 = nn.ReLU()

        # First 3x3 Convolution + BatchNorm + ReLU
        self.conv3x3_1 = nn.Conv2d(mid_channels, mid_channels, kernel_size=3, padding=1)
        self.bn4 = nn.BatchNorm2d(mid_channels)
        self.relu4 = nn.ReLU()

        # Second 3x3 Convolution + BatchNorm + ReLU
        self.conv3x3_2 = nn.Conv2d(mid_channels, out_channels, kernel_size=3, padding=1)
        self.bn5 = nn.BatchNorm2d(out_channels)
        self.relu5 = nn.ReLU()

        # MaxPooling 2x2
        self.maxpool = nn.MaxPool2d(kernel_size=2, stride=2)

        # Skip connection (Conv2D 1x1 + BatchNorm)
        self.skip_conv = nn.Conv2d(in_channels, out_channels, kernel_size=1)
        self.skip_bn = nn.BatchNorm2d(out_channels)

    def forward(self, x):
        identity = self.skip_bn(self.skip_conv(x))  # Skip connection

        x = self.conv2x2(x)
        x = self.bn1(x)
        x = self.relu1(x)

        x = self.upsample(x)

        x = self.depthwise(x)
        x = self.bn2(x)
        x = self.relu2(x)

        x = self.pointwise(x)
        x = self.bn3(x)
        x = self.relu3(x)

        x = self.conv3x3_1(x)
        x = self.bn4(x)
        x = self.relu4(x)

        x = self.conv3x3_2(x)
        x = self.bn5(x)
        x = self.relu5(x)

        x = self.maxpool(x)

        # Add skip connection
        x += identity
        return x


# ---------------------------
# 🟣 5. MDSCIRNet Model (Updated)
# ---------------------------
class MDSCIRNet(nn.Module):
    def __init__(self, num_classes=5):
        super(MDSCIRNet, self).__init__()
        
        # Stem Block
        self.stem = StemBlock(in_channels=3, out_channels=64)

        # Block 1
        self.scrda1_1 = SCRDA_Block(64, 384, 192)
        self.scrda1_2 = SCRDA_Block(192, 384, 192)
        self.scrda1_3 = SCRDA_Block(192, 384, 192)
        self.scrdb1_4 = SCRDB_Block(192, 384, 192)
        self.trans1 = TransitionLayer(192, 12)

        # Block 2
        self.scrda2_1 = SCRDA_Block(12, 216, 108)
        self.scrda2_2 = SCRDA_Block(108, 216, 108)
        self.scrda2_3 = SCRDA_Block(108, 216, 108)
        self.scrdb2_4 = SCRDB_Block(108, 216, 108)
        self.trans2 = TransitionLayer(108, 8)

        # Block 3
        self.scrda3_1 = SCRDA_Block(8, 144, 72)
        self.scrda3_2 = SCRDA_Block(72, 144, 72)
        self.scrda3_3 = SCRDA_Block(72, 144, 72)
        self.scrdb3_4 = SCRDB_Block(72, 144, 72)
        self.trans3 = TransitionLayer(72, 6)

        # Block 4
        self.scrda4_1 = SCRDA_Block(6, 108, 54)
        self.scrda4_2 = SCRDA_Block(54, 108, 54)
        self.scrda4_3 = SCRDA_Block(54, 108, 54)
        self.scrdb4_4 = SCRDB_Block(54, 108, 54)
        self.trans4 = TransitionLayer(54, 4)

        # Block 5
        self.scrda5_1 = SCRDA_Block(4, 72, 36)
        self.scrda5_2 = SCRDA_Block(36, 72, 36)
        self.scrda5_3 = SCRDA_Block(36, 72, 36)
        self.scrdb5_4 = SCRDB_Block(36, 72, 36)

        # Global Average Pooling
        self.global_pool = nn.AdaptiveAvgPool2d(1)

        # Fully Connected Layer
        self.fc = nn.Linear(12 + 8 + 6 + 4 + 36, num_classes)  # Summing up the final layer channels

    def forward(self, x):
        x = self.stem(x)

        # Block 1
        x1 = self.scrda1_1(x)
        x1 = self.scrda1_2(x1)
        x1 = self.scrda1_3(x1)
        x1 = self.scrdb1_4(x1)
        x1 = self.trans1(x1)

        # Block 2
        x2 = self.scrda2_1(x1)
        x2 = self.scrda2_2(x2)
        x2 = self.scrda2_3(x2)
        x2 = self.scrdb2_4(x2)
        x2 = self.trans2(x2)

        # Block 3
        x3 = self.scrda3_1(x2)
        x3 = self.scrda3_2(x3)
        x3 = self.scrda3_3(x3)
        x3 = self.scrdb3_4(x3)
        x3 = self.trans3(x3)

        # Block 4
        x4 = self.scrda4_1(x3)
        x4 = self.scrda4_2(x4)
        x4 = self.scrda4_3(x4)
        x4 = self.scrdb4_4(x4)
        x4 = self.trans4(x4)

        # Block 5
        x5 = self.scrda5_1(x4)
        x5 = self.scrda5_2(x5)
        x5 = self.scrda5_3(x5)
        x5 = self.scrdb5_4(x5)

        # Global Average Pooling
        x = self.global_pool(x5)
        x = torch.flatten(x, 1)

        # Fully Connected
        x = self.fc(x)

        return x
