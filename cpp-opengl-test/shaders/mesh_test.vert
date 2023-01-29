//顶点着色器
#version 300 es                            
layout(location = 0) in vec4 a_position;// 位置变量的属性位置值为 0
layout(location = 1) in vec3 a_color;// 颜色变量的属性位置值为 1
out vec3 v_color;// 向片段着色器输出一个颜色
void main()
{
    v_color = a_color;
    gl_Position = a_position;
}