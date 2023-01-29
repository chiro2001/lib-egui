//片段着色器
#version 300 es
precision mediump float;
in vec3 v_color;
out vec4 o_fragColor;
void main()
{
    o_fragColor = vec4(v_color, 1.0);
}