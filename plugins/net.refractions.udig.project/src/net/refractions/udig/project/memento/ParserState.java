package net.refractions.udig.project.memento;

import static net.refractions.udig.project.memento.Tokens.__null__;
import static net.refractions.udig.project.memento.Tokens._children_;
import static net.refractions.udig.project.memento.Tokens._data_;
import static net.refractions.udig.project.memento.Tokens._memento_;
import static net.refractions.udig.project.memento.Tokens._none_;
import static net.refractions.udig.project.memento.Tokens._text_;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum ParserState {
    OUT(_none_) {
        @Override
        void parse( UdigMemento memento, BufferedReader reader, String context ) throws IOException {
            String line = removeWhite(reader.readLine());
            
            if( line.equals(_memento_+"{") ){
                MEMENTO.parse(memento, reader, null);
            }
        }
    },
    MEMENTO(_memento_) {
        @Override
        void parse( UdigMemento memento, BufferedReader reader, String context ) throws IOException {
            String line = removeWhite(reader.readLine());
            while( !line.equals("}") ){
                for( ParserState state : ParserState.values() ) {
                    if( line.startsWith(state.token.name()) ){
                        state.parse(memento,reader, null);
                        break;
                    }

                }
                line = removeWhite(reader.readLine());
            }
        }

    },
    DATA(_data_) {
        final Pattern PATTERN = Pattern.compile("\\s*\\|(.*)\\|.*\\{"); 
        @Override
        void parse( UdigMemento memento, BufferedReader reader, String context ) throws IOException {
            String line = reader.readLine();
            while( !removeWhite(line).equals("}") ){
                Matcher matcher = PATTERN.matcher(line);
                if(matcher.find()){
                    String key = checkNullToken(matcher.group(1));
                    String value = checkNullToken(parseValue(reader));
                    memento.putString(key,value);
                }
                line = reader.readLine();
            }
        }
    },
    CHILDREN(_children_) {
        @Override
        void parse( UdigMemento memento, BufferedReader reader, String context ) throws IOException {
            String line = removeWhite(reader.readLine());
            while( !line.equals("}") ){
                TYPE.parse(memento, reader, line);

                line = removeWhite(reader.readLine());
            }
        }
    },
    TEXT(_text_) {
        @Override
        void parse( UdigMemento memento, BufferedReader reader, String context ) throws IOException {
            memento.putTextData(parseValue(reader));
        }
    },
    TYPE(_none_) {
        @Override
        void parse( UdigMemento memento, BufferedReader reader, String context ) throws IOException {
            String typeName =checkNullToken(context.substring(0, context.indexOf("{")));
            String line = removeWhite(reader.readLine());
            while( !line.equals("}") ){
                if(line.equals(_memento_+"{")){
                    UdigMemento newMem = memento.createChild(typeName);
                    MEMENTO.parse(newMem, reader, null);
                }
                line = removeWhite(reader.readLine());
            }
        }
    };
    
    private Tokens token;

    private ParserState(Tokens token){
        this.token = token;
    }

    protected String parseValue( BufferedReader reader ) throws IOException {
        String line = reader.readLine();
        StringBuilder val = new StringBuilder();
        boolean isFirst = true;
        while( !removeWhite(line).equals("}") ){
            if(isFirst){
                isFirst=false;
            }else{
                val.append("\n");
            }
            val.append(line);
            line = reader.readLine();
        }

        return val.toString();
    }

    abstract void parse( UdigMemento memento, BufferedReader reader, String context ) throws IOException;
    
    private static String removeWhite( String line ) {
        if( line==null ){
            return "";
        }
        return line.replaceAll("\\s*","");
    }
    private static String checkNullToken( String value ) {
        if( value.equals(__null__.name()) ){
            value=null;
        }
        return value;
    }

}